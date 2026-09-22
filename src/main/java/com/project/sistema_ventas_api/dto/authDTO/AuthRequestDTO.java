package com.project.sistema_ventas_api.dto.authDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequestDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
