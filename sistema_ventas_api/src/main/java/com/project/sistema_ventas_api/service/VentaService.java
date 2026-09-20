package com.project.sistema_ventas_api.service;

import com.project.sistema_ventas_api.dto.ventaDTO.VentaRequestDTO;
import com.project.sistema_ventas_api.dto.ventaDTO.VentaResponseDTO;
import com.project.sistema_ventas_api.entity.DetalleVenta;
import com.project.sistema_ventas_api.entity.Producto;
import com.project.sistema_ventas_api.entity.Venta;
import com.project.sistema_ventas_api.mapper.VentaMapper;
import com.project.sistema_ventas_api.repository.ProductoRepository;
import com.project.sistema_ventas_api.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

        //Obtenemos los productos que el cliente quiere comprar
        List<DetalleVenta> detallesVenta = ventaMapper.toEntity(requestDTO).getDetalles();

        //Creamos los detalles de los productos aceptados que cuenten con el stock y lo necesario para la venta
        List<DetalleVenta> detallesAceptados = new ArrayList<>();

        for (DetalleVenta detalleVenta : detallesVenta)
        {
            //Buscamos el producto de cada detalle
            Producto productoEncontrado = productoRepository.findById(detalleVenta.getProducto().getId()).orElseThrow(() -> new RuntimeException("Producto no encontrado " + detalleVenta.getProducto().getId()));

            //Si el producto no existe se envia el error
            if(productoEncontrado.getStock() < detalleVenta.getCantidad())
                throw new IllegalArgumentException("No hay stock suficiente para el producto " + productoEncontrado.getNombre());

            //Si el producto sí existe
            BigDecimal subTotal = productoEncontrado.getPrecio().multiply(new BigDecimal(detalleVenta.getCantidad())); //Calculamos subTotal
            total = total.add(subTotal); //Sumamos los subtotales para tener el total

            detalleVenta.setPrecioUnitario(productoEncontrado.getPrecio()); //Guardamos el precio unitario
            detalleVenta.setSubTotal(subTotal); //guardamos el subtotal
            detalleVenta.setVenta(venta); //Anclamos la venta a sus detalles

            productoEncontrado.setStock(productoEncontrado.getStock() - detalleVenta.getCantidad()); //Descontamos lo vendido al stock

            detallesAceptados.add(detalleVenta); //Guardamos los detalles

        }

        //Guardamos el total y los detalles de la venta realizada
        venta.setTotal(total);
        venta.setDetalles(detallesAceptados);

        //No se necesita hacer un update al producto, jpa lo guarda automaticamente

        //Guardamos la venta
        return ventaMapper.toDto(ventaRepository.save(venta));
    }
}
