package com.project.sistema_ventas_api.controller;

import com.project.sistema_ventas_api.dto.authDTO.AuthRequestDTO;
import com.project.sistema_ventas_api.dto.authDTO.AuthResponseDTO;
import com.project.sistema_ventas_api.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtUtil jwtUtil)
    {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO requestDTO)
    {
        //Validamos credenciales
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.getUsername(), requestDTO.getPassword())
        );

        //si la contraseña es correcta, traemos el perfil del usuario
        UserDetails userDetails = userDetailsService.loadUserByUsername(requestDTO.getUsername());

        //obtenemos el rol
        String rol = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        //fabricamos el token
        String jwtToken = jwtUtil.generarToken(userDetails.getUsername(), rol);

        AuthResponseDTO response = new AuthResponseDTO();

        response.setToken(jwtToken);

        return ResponseEntity.ok(response);

    }



}
