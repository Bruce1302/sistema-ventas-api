package com.project.sistema_ventas_api.mapper;


import com.project.sistema_ventas_api.dto.ventaDTO.VentaRequestDTO;
import com.project.sistema_ventas_api.dto.ventaDTO.VentaResponseDTO;
import com.project.sistema_ventas_api.entity.Venta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DetalleVentaMapper.class})
public interface VentaMapper {

    @Mapping(target = "cantidadProductosVendidos", ignore = true)
    VentaResponseDTO toDto(Venta venta);

    Venta toEntity(VentaRequestDTO resquestDTO);
}
