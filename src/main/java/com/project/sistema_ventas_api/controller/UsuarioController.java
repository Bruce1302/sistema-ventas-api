package com.project.sistema_ventas_api.controller;

import com.project.sistema_ventas_api.dto.usuarioDTO.UsuarioRequestDTO;
import com.project.sistema_ventas_api.dto.usuarioDTO.UsuarioResponseDTO;
import com.project.sistema_ventas_api.service.UsuarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService)
    {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public UsuarioResponseDTO nuevoUsuario(@RequestBody UsuarioRequestDTO requestDTO)
    {
       return usuarioService.nuevoUsuario(requestDTO);
    }

    @GetMapping
    public List<UsuarioResponseDTO> listarUsuarios()
    {
        return usuarioService.listarUsuarios();
    }

    @DeleteMapping("/{id}")
    public void eliminarUsuario(@PathVariable UUID id)
    {
        usuarioService.eliminarUsuario(id);
    }
}
