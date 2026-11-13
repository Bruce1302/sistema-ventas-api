package com.project.sistema_ventas_api.mapper;

import com.project.sistema_ventas_api.dto.productoDTO.ProductoResponseDTO;
import com.project.sistema_ventas_api.dto.tiendaDTO.TiendaRequestDTO;
import com.project.sistema_ventas_api.dto.tiendaDTO.TiendaResponseDTO;
import com.project.sistema_ventas_api.entity.Producto;
import com.project.sistema_ventas_api.entity.Tienda;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TiendaMapper {

    Tienda toEntity( TiendaRequestDTO tiendaRequestDTO);

    TiendaResponseDTO toDto(Tienda tienda);

    List<TiendaResponseDTO> toDtoList(List<Tienda> tiendas);
}
