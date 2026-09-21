package com.project.sistema_ventas_api.dto.detalleVentaDTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleVentaResponseDTO {

    private String id;
    private String productoId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subTotal;
}
