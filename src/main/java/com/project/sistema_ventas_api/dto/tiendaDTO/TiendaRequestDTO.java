package com.project.sistema_ventas_api.dto.tiendaDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TiendaRequestDTO {

    @NotBlank
    private String codigo;

    @NotBlank
    private String nombre;

    @NotBlank
    private String direccion;

    private String telefono;
}
