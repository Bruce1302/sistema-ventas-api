package com.project.sistema_ventas_api.dto.productoDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoRequestDTO {

    @NotBlank
    private String nombre;

    @NotBlank
    private String categoriaId;

    @NotNull
    @Positive
    private BigDecimal precio;

    @NotNull
    @PositiveOrZero
    private Integer stock;

}
