package com.project.sistema_ventas_api.service;

import com.project.sistema_ventas_api.dto.ventaDTO.VentaRequestDTO;
import com.project.sistema_ventas_api.dto.ventaDTO.VentaResponseDTO;
import com.project.sistema_ventas_api.entity.DetalleVenta;
import com.project.sistema_ventas_api.entity.Producto;
import com.project.sistema_ventas_api.entity.Venta;
import com.project.sistema_ventas_api.exception.RecursoNoEncontradoException;
import com.project.sistema_ventas_api.mapper.VentaMapper;
import com.project.sistema_ventas_api.repository.ProductoRepository;
import com.project.sistema_ventas_api.repository.VentaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final VentaMapper ventaMapper;

    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository, VentaMapper ventaMapper)
    {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.ventaMapper = ventaMapper;
    }

    @Transactional
    public VentaResponseDTO registrarVenta(VentaRequestDTO requestDTO)
    {
        //Creamos el nuevo registro de venta
        Venta venta = new Venta();

        BigDecimal total = BigDecimal.ZERO;
        int cantidadProductosVendidos = 0;

        //Obtenemos los productos que el cliente quiere comprar
        List<DetalleVenta> detallesVenta = ventaMapper.toEntity(requestDTO).getDetalles();

        //Creamos los detalles de los productos aceptados que cuenten con el stock y lo necesario para la venta
        List<DetalleVenta> detallesAceptados = new ArrayList<>();

        for (DetalleVenta detalleVenta : detallesVenta)
        {
            //Buscamos el producto de cada detalle
            Producto productoEncontrado = productoRepository.findById(detalleVenta.getProducto().getId()).orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado " + detalleVenta.getProducto().getId()));

            //Si el producto no existe se envia el error
            if(productoEncontrado.getStock() < detalleVenta.getCantidad())
                throw new IllegalArgumentException("No hay stock suficiente para el producto " + productoEncontrado.getNombre());

            //Si el producto sí existe
            BigDecimal subTotal = productoEncontrado.getPrecio().multiply(new BigDecimal(detalleVenta.getCantidad())); //Calculamos subTotal
            total = total.add(subTotal); //Sumamos los subtotales para tener el total

            detalleVenta.setPrecioUnitario(productoEncontrado.getPrecio()); //Guardamos el precio unitario
            detalleVenta.setSubTotal(subTotal); //guardamos el subtotal
            detalleVenta.setVenta(venta); //Anclamos la venta a sus detalles

            cantidadProductosVendidos += detalleVenta.getCantidad();

            productoEncontrado.setStock(productoEncontrado.getStock() - detalleVenta.getCantidad()); //Descontamos lo vendido al stock

            detallesAceptados.add(detalleVenta); //Guardamos los detalles

        }

        //Guardamos el total y los detalles de la venta realizada
        venta.setTotal(total);
        venta.setDetalles(detallesAceptados);

        //Guardamos la venta y recibimos el response
        VentaResponseDTO dto = ventaMapper.toDto(ventaRepository.save(venta));

        //LLenamos el dato faltante
        dto.setCantidadProductosVendidos(cantidadProductosVendidos);

        //No se necesita hacer un update al producto, jpa lo guarda automaticamente

        //NO se necesita guardar manualmente los detalles en la bd, al haber una relacion en las entidades, se hace automaticamente

        return dto;
    }

    @Transactional(readOnly = true)
    public Page<VentaResponseDTO> listarVentas(int page, int size)
    {
        //solicitud de paginacion
        Pageable pageable = PageRequest.of(page, size);

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
}
