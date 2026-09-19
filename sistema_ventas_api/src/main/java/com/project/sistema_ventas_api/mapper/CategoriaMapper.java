package com.project.sistema_ventas_api.mapper;

import com.project.sistema_ventas_api.dto.categoriaDTO.CategoriaRequestDTO;
import com.project.sistema_ventas_api.dto.categoriaDTO.CategoriaResponseDTO;
import com.project.sistema_ventas_api.entity.Categoria;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    CategoriaResponseDTO toDto(Categoria categoria);

    Categoria toEntity(CategoriaRequestDTO requestDTO);
}
