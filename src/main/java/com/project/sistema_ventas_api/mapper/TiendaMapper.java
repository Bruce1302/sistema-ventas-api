package com.project.sistema_ventas_api.mapper;

import com.project.sistema_ventas_api.dto.tiendaDTO.TiendaRequestDTO;
import com.project.sistema_ventas_api.dto.tiendaDTO.TiendaResponseDTO;
import com.project.sistema_ventas_api.entity.Tienda;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TiendaMapper {

    Tienda toEntity( TiendaRequestDTO tiendaRequestDTO);

    TiendaResponseDTO toDto(Tienda tienda);
}
