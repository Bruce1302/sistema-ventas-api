package com.project.sistema_ventas_api.mapper;


import com.project.sistema_ventas_api.dto.categoriaDTO.CategoriaResponseDTO;
import com.project.sistema_ventas_api.dto.ventaDTO.VentaRequestDTO;
import com.project.sistema_ventas_api.dto.ventaDTO.VentaResponseDTO;
import com.project.sistema_ventas_api.entity.Categoria;
import com.project.sistema_ventas_api.entity.Venta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {DetalleVentaMapper.class})
public interface VentaMapper {

    @Mapping(target = "cantidadProductosVendidos", ignore = true)
    VentaResponseDTO toDto(Venta venta);

    Venta toEntity(VentaRequestDTO resquestDTO);

    List<VentaResponseDTO> toDtoList(List<Venta> ventas);
}
