package com.project.sistema_ventas_api.dto.detalleVentaDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DetalleVentaRequestDTO {

    @NotBlank
    private String productoId;

    @NotNull
    @Positive
    private Integer cantidad;
}

