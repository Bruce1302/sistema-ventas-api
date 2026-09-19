package com.project.sistema_ventas_api.dto.productoDTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoResponseDTO {

    private String id;
    private String nombre;
    private String categoriaId; //Categoria objeto
    private BigDecimal precio;
    private int stock;
}
