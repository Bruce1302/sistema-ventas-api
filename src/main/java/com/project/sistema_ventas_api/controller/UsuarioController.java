package com.project.sistema_ventas_api.controller;

import com.project.sistema_ventas_api.dto.usuarioDTO.UsuarioRequestDTO;
import com.project.sistema_ventas_api.dto.usuarioDTO.UsuarioResponseDTO;
import com.project.sistema_ventas_api.service.UsuarioService;
import jakarta.validation.Valid;
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
    public UsuarioResponseDTO nuevoUsuario( @Valid @RequestBody UsuarioRequestDTO requestDTO)
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

    @PutMapping("{id}")
    public UsuarioResponseDTO actualizarUsuario (@Valid @RequestBody UsuarioRequestDTO requestDTO, @PathVariable UUID id)
    {
        return usuarioService.actualizarUsuario(requestDTO, id);
    }
}
