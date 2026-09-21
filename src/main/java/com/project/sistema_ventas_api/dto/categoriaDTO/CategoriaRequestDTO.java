package com.project.sistema_ventas_api.dto.categoriaDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoriaRequestDTO {

    @NotBlank
    private String nombre;

}
