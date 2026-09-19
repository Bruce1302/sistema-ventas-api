package com.project.sistema_ventas_api.dto.usuarioDTO;

import com.project.sistema_ventas_api.enums.RolUsuario;
import lombok.Data;

@Data
public class UsuarioResponseDTO {

    private String id;
    private String username;
    private RolUsuario rol;
}
