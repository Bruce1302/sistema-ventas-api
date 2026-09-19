package com.project.sistema_ventas_api.mapper;

import com.project.sistema_ventas_api.dto.detalleVentaDTO.DetalleVentaRequestDTO;
import com.project.sistema_ventas_api.dto.detalleVentaDTO.DetalleVentaResponseDTO;
import com.project.sistema_ventas_api.entity.DetalleVenta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DetalleVentaMapper {

    @Mapping(source = "producto.id", target = "productoId")
    DetalleVentaResponseDTO toDto(DetalleVenta detalleVenta);

    @Mapping(source = "productoId", target = "producto.id")
    DetalleVenta toEntity(DetalleVentaRequestDTO requestDTO);
}
