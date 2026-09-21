package com.project.sistema_ventas_api.mapper;

import com.project.sistema_ventas_api.dto.categoriaDTO.CategoriaResponseDTO;
import com.project.sistema_ventas_api.dto.productoDTO.ProductoRequestDTO;
import com.project.sistema_ventas_api.dto.productoDTO.ProductoResponseDTO;
import com.project.sistema_ventas_api.entity.Categoria;
import com.project.sistema_ventas_api.entity.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    @Mapping(source = "categoria.id", target = "categoriaId") //En
    ProductoResponseDTO toDto(Producto producto);

    @Mapping(source = "categoriaId", target = "categoria.id")
    Producto toEntity(ProductoRequestDTO resquestDTO);

     List<ProductoResponseDTO> toDtoList(List<Producto> productos);
}
