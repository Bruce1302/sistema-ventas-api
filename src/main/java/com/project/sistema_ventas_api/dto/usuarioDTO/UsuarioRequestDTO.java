package com.project.sistema_ventas_api.dto.usuarioDTO;

import com.project.sistema_ventas_api.enums.RolUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsuarioRequestDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotNull
    private RolUsuario rol;

}
