package com.project.sistema_ventas_api.service;

import com.project.sistema_ventas_api.dto.ventaDTO.VentaRequestDTO;
import com.project.sistema_ventas_api.dto.ventaDTO.VentaResponseDTO;
import com.project.sistema_ventas_api.entity.DetalleVenta;
import com.project.sistema_ventas_api.entity.Producto;
import com.project.sistema_ventas_api.entity.Tienda;
import com.project.sistema_ventas_api.entity.Venta;
import com.project.sistema_ventas_api.enums.EstadoSunat;
import com.project.sistema_ventas_api.enums.TipoComprobante;
import com.project.sistema_ventas_api.exception.RecursoNoEncontradoException;
import com.project.sistema_ventas_api.mapper.VentaMapper;
import com.project.sistema_ventas_api.repository.ProductoRepository;
import com.project.sistema_ventas_api.repository.TiendaRepository;
import com.project.sistema_ventas_api.repository.VentaRepository;
import com.project.sistema_ventas_api.security.TenantContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final VentaMapper ventaMapper;
    private final TiendaRepository tiendaRepository;
    private final FacturacionService facturacionService;
    private final TransactionTemplate transactionTemplate;

    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository, VentaMapper ventaMapper, TiendaRepository tiendaRepository, FacturacionService facturacionService, TransactionTemplate transactionTemplate)
    {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.ventaMapper = ventaMapper;
        this.tiendaRepository = tiendaRepository;
        this.facturacionService = facturacionService;
        this.transactionTemplate = transactionTemplate;
    }

    public VentaResponseDTO registrarVenta(VentaRequestDTO requestDTO, String idempotencyKey)
    {
        String keyLimpia = (idempotencyKey != null && !idempotencyKey.isBlank()) ? idempotencyKey.trim() : null;

        if (keyLimpia != null)
        {
            Optional<Venta> ventaExistente = transactionTemplate.execute(status -> {
                Optional<Venta> opt = ventaRepository.findByIdempotencyKey(keyLimpia);
                opt.ifPresent(v -> v.getDetalles().size()); // Inicializamos detalles antes de cerrar la sesión
                return opt;
            });

            if (ventaExistente != null && ventaExistente.isPresent())
                return mapearVentaCompleta(ventaExistente.get());

        }

        //  Validaciones previas
        if (requestDTO.getDetalles() == null || requestDTO.getDetalles().isEmpty())
            throw new IllegalArgumentException("La venta debe tener al menos un producto.");

        for (var item : requestDTO.getDetalles())
            if (item.getProductoId() == null || item.getCantidad() == null || item.getCantidad() <= 0)
                throw new IllegalArgumentException("Cada detalle debe tener productoId y cantidad mayor a 0.");

        String tiendaIdActual = TenantContext.getCurrentTenant();
        Tienda tienda = tiendaRepository.findById(tiendaIdActual).orElseThrow(() -> new RecursoNoEncontradoException("Tienda no encontrada"));

        TipoComprobante tipoComp = requestDTO.getTipoComprobante() != null ? requestDTO.getTipoComprobante() : TipoComprobante.NOTA_VENTA;

        if (tipoComp == TipoComprobante.FACTURA
                && (!"6".equals(requestDTO.getClienteTipoDoc()) || requestDTO.getClienteNumDoc() == null || !requestDTO.getClienteNumDoc().trim().matches("\\d{11}")))
            throw new IllegalArgumentException("Para emitir Factura es obligatorio el RUC del cliente.");

        // Transacción corta se nloquea stock, descuenta y guarda venta
        Venta ventaGuardada;
        try
        {
            ventaGuardada = transactionTemplate.execute(status -> {
                Tienda tiendaRef = tiendaRepository.getReferenceById(tiendaIdActual);
                Venta venta = new Venta();
                BigDecimal total = BigDecimal.ZERO;

                List<DetalleVenta> detallesVenta = ventaMapper.toEntity(requestDTO).getDetalles();
                detallesVenta.sort(Comparator.comparing(d -> d.getProducto().getId()));

                List<DetalleVenta> detallesAceptados = new ArrayList<>();

                for (DetalleVenta detalleVenta : detallesVenta)
                {
                    Producto productoEncontrado = productoRepository.findByIdConBloqueo(detalleVenta.getProducto().getId())
                            .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado " + detalleVenta.getProducto().getId()));

                    if (productoEncontrado.getStock() < detalleVenta.getCantidad())
                        throw new IllegalArgumentException("No hay stock suficiente para el producto " + productoEncontrado.getNombre());


                    BigDecimal subTotal = productoEncontrado.getPrecio().multiply(BigDecimal.valueOf(detalleVenta.getCantidad()));
                    total = total.add(subTotal);

                    detalleVenta.setPrecioUnitario(productoEncontrado.getPrecio());
                    detalleVenta.setSubTotal(subTotal);
                    detalleVenta.setVenta(venta);
                    detalleVenta.setTienda(tiendaRef);
                    detalleVenta.setProducto(productoEncontrado);

                    productoEncontrado.setStock(productoEncontrado.getStock() - detalleVenta.getCantidad());
                    detallesAceptados.add(detalleVenta);
                }

                venta.setTotal(total);
                venta.setDetalles(detallesAceptados);
                venta.setTienda(tiendaRef);
                venta.setTipoComprobante(tipoComp);
                venta.setClienteTipoDoc(requestDTO.getClienteTipoDoc());
                venta.setClienteNumDoc(requestDTO.getClienteNumDoc());
                venta.setClienteNombre(requestDTO.getClienteNombre());

                venta.setIdempotencyKey(keyLimpia);

                return ventaRepository.save(venta);
            });

        }
        catch (DataIntegrityViolationException ex)
        {
            //cuando llegue 2 veces la misma venta
            if (keyLimpia != null)
            {
                Venta existente = transactionTemplate.execute(status -> {
                    Venta v = ventaRepository.findByIdempotencyKey(keyLimpia).orElseThrow(() -> ex);
                    v.getDetalles().size();
                    return v;
                });
                return mapearVentaCompleta(existente);
            }
            throw ex;
        }


        // Facturación externa fuera de transaccion
        facturacionService.emitirComprobante(ventaGuardada, tienda);

        // Microtransaccin para guardar respuesta SUNAT y mapear DTO
        return transactionTemplate.execute(status -> {
            Venta ventaFinal = ventaRepository.save(ventaGuardada);
            return mapearVentaCompleta(ventaFinal);
        });
    }

    private VentaResponseDTO mapearVentaCompleta(Venta venta)
    {
        VentaResponseDTO dto = ventaMapper.toDto(venta);
        int cantidadProductosVendidos = venta.getDetalles().stream()
                .mapToInt(DetalleVenta::getCantidad)
                .sum();
        dto.setCantidadProductosVendidos(cantidadProductosVendidos);
        return dto;
    }

    @Transactional(readOnly = true)
    public byte[] obtenerPdfComprobante(UUID ventaId, String formato)
    {
        Venta venta = ventaRepository.findById(ventaId.toString()).orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada"));

        if (venta.getExternalId() == null || venta.getExternalId().isBlank())
            throw new IllegalArgumentException("Esta venta no tiene un comprobante electrónico emitido.");

        return facturacionService.descargarPdf(venta.getExternalId(), venta.getTienda(), formato);
    }

    @Transactional(readOnly = true)
    public Page<VentaResponseDTO> listarVentas(int page, int size)
    {
        //solicitud de paginacion
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());

        Page<Venta> paginaVentas = ventaRepository.findAllByActivo(true, pageable);

        return paginaVentas.map(venta -> {
            VentaResponseDTO dto = ventaMapper.toDto(venta);
            dto.setCantidadProductosVendidos(venta.getDetalles().size());
            return dto;
        });
    }

    @Transactional
    public void eliminarVenta(UUID id)
    {
        ventaRepository.findById(id.toString()).orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada"));
        ventaRepository.deleteById(id.toString());
    }

    @Transactional
    public VentaResponseDTO actualizarVenta(UUID id, VentaRequestDTO requestDTO)
    {
        Venta ventaEncontrada = ventaRepository.findById(id.toString()).orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        ventaEncontrada.setDetalles(ventaMapper.toEntity(requestDTO).getDetalles());
        ventaEncontrada.setTotal(ventaMapper.toEntity(requestDTO).getTotal());

        return ventaMapper.toDto(ventaRepository.save(ventaEncontrada));
    }

    @Transactional(readOnly = true)
    public VentaResponseDTO buscarVentaPorId(UUID id)
    {
        Venta venta = ventaRepository.findById(id.toString()).orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada"));

        VentaResponseDTO dto = ventaMapper.toDto(venta);
        dto.setCantidadProductosVendidos(venta.getDetalles().size());

        return dto;
    }

    @Transactional(readOnly = true)
    public Page<VentaResponseDTO> filtrarVentasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin, int page, int size)
    {
        if (fechaInicio == null || fechaFin == null)
            return listarVentas(0, 10);

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());

        Page<Venta> ventasEncontradas = ventaRepository.findByFechaCreacionBetween(fechaInicio, fechaFin, pageable);

        return ventasEncontradas.map(venta -> {
            VentaResponseDTO dto = ventaMapper.toDto(venta);
            dto.setCantidadProductosVendidos(venta.getDetalles().size());
            return dto;
        });

    }

    public VentaResponseDTO reenviarVentaSunat(String ventaId)
    {
        // Leemos la venta y su tienda en una microtransaccion rápida
        Venta venta = transactionTemplate.execute(status -> {
            Venta v = ventaRepository.findById(ventaId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada"));

            if (v.getEstadoSunat() != EstadoSunat.PENDIENTE)
                throw new IllegalArgumentException("Solo se pueden reenviar comprobantes en estado PENDIENTE.");

            // Inicializamos tienda y detalles antes de cerrar la sesión de Hibernate
            v.getTienda().getRuc();
            v.getDetalles().size();
            return v;
        });

        // Fallo el internet antes de llegar a FactuSmart -> Emitimos por primera vez (usa Idempotency-Key)
        if (venta.getExternalId() == null || venta.getExternalId().isBlank())
            facturacionService.emitirComprobante(venta, venta.getTienda());

        // Si llego a FactuSmart, pero SUNAT estaba caída -> Reenviamos el existente
        else
            facturacionService.reenviarComprobante(venta, venta.getTienda());

        // Guardamos el nuevo estado y mapeamos el DTO dentro de otra microtransaccion
        return transactionTemplate.execute(status -> {
            Venta ventaActualizada = ventaRepository.save(venta);
            VentaResponseDTO dto = ventaMapper.toDto(ventaActualizada);
            dto.setCantidadProductosVendidos(
                    ventaActualizada.getDetalles().stream()
                            .mapToInt(DetalleVenta::getCantidad)
                            .sum()
            );
            return dto;
        });
    }
}
