package com.project.sistema_ventas_api.mapper;

import com.project.sistema_ventas_api.dto.usuarioDTO.UsuarioRequestDTO;
import com.project.sistema_ventas_api.dto.usuarioDTO.UsuarioResponseDTO;

import com.project.sistema_ventas_api.entity.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    //Convertimos la salida a dto
    UsuarioResponseDTO toDto(Usuario usuario);

    //Convertimos la entrada a dto
    Usuario toEntity(UsuarioRequestDTO requestDTO);
}
