package com.project.sistema_ventas_api.dto.ventaDTO;

import com.project.sistema_ventas_api.dto.detalleVentaDTO.DetalleVentaResponseDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VentaResponseDTO {

    private String id;
    private BigDecimal total;
    private List<DetalleVentaResponseDTO> detalles;
    private int cantidadProductosVendidos;
    private LocalDateTime fechaVenta;
}
