package com.project.sistema_ventas_api.controller;

import com.project.sistema_ventas_api.dto.authDTO.AuthRequestDTO;
import com.project.sistema_ventas_api.dto.authDTO.AuthResponseDTO;
import com.project.sistema_ventas_api.entity.Usuario;
import com.project.sistema_ventas_api.repository.UsuarioRepository;
import com.project.sistema_ventas_api.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil)
    {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO requestDTO)
    {

        // Buscamos al usuario exigiendo que el username Y el codigo de tienda coincidan
        Usuario usuario = usuarioRepository.findByUsernameAndTienda_CodigoAndActivo(
                requestDTO.getUsername(), requestDTO.getCodigoTienda(), true
        ).orElseThrow(() -> new BadCredentialsException("Credenciales inválidas o tienda incorrecta"));

        // Verificamos la contraseña encriptada manualmente
        if (!passwordEncoder.matches(requestDTO.getPassword(), usuario.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas o tienda incorrecta");
        }

        // Obtenemos el rol y el ID de la tienda
        String rol = usuario.getRol().name();
        String tiendaId = usuario.getTienda().getId();

        // Fabricamos el token
        String jwtToken = jwtUtil.generarToken(usuario.getUsername(), rol, tiendaId);

        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(jwtToken);

        return ResponseEntity.ok(response);
    }

}
