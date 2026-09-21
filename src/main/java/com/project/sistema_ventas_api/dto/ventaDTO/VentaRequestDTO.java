package com.project.sistema_ventas_api.dto.ventaDTO;

import com.project.sistema_ventas_api.dto.detalleVentaDTO.DetalleVentaRequestDTO;
import com.project.sistema_ventas_api.entity.DetalleVenta;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class VentaRequestDTO {

    @NotNull
    private List<DetalleVentaRequestDTO> detalles;

}
