package com.project.sistema_ventas_api.dto.tiendaDTO;

import lombok.Data;

@Data
public class TiendaResponseDTO {

    private String id;

    private String codigo;

    private String nombre;

    private String direccion;

    private String telefono;
}
