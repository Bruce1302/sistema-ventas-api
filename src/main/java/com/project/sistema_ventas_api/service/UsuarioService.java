package com.project.sistema_ventas_api.service;

import com.project.sistema_ventas_api.dto.usuarioDTO.UsuarioRequestDTO;
import com.project.sistema_ventas_api.dto.usuarioDTO.UsuarioResponseDTO;
import com.project.sistema_ventas_api.entity.Usuario;
import com.project.sistema_ventas_api.exception.RecursoNoEncontradoException;
import com.project.sistema_ventas_api.mapper.UsuarioMapper;
import com.project.sistema_ventas_api.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder)
    {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponseDTO nuevoUsuario(UsuarioRequestDTO requestDTO)
    {
        //Buscamos el username para no duplicarlo
        if (usuarioRepository.findByUsername(requestDTO.getUsername()).isPresent())
            throw new IllegalArgumentException("El username '" + requestDTO.getUsername() + "' ya está en uso. Elige otro.");

        Usuario usuario = usuarioMapper.toEntity(requestDTO);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword())); //Encriptamos la contraseña antes de gaurdar

        return usuarioMapper.toDto(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarUsuarios()
    {
        return  usuarioMapper.toDtoList(usuarioRepository.findAllByActivo(true));
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarUsuarioPorId(UUID id)
    {
        return usuarioMapper.toDto(usuarioRepository.findById(id.toString()).orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado")));
    }

    @Transactional
    public void eliminarUsuario(UUID id)
    {
        Usuario usuarioEncontrado =  usuarioRepository.findById(id.toString()).orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        usuarioEncontrado.setActivo(false);
        usuarioRepository.save(usuarioEncontrado);
    }

    @Transactional
    public UsuarioResponseDTO actualizarUsuario(UsuarioRequestDTO requestDTO, UUID id)
    {
        Usuario usuarioEncontrado = usuarioRepository.findById(id.toString()).orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        usuarioEncontrado.setUsername(requestDTO.getUsername());
        usuarioEncontrado.setPassword(requestDTO.getPassword());
        usuarioEncontrado.setRol(requestDTO.getRol());

        usuarioRepository.save(usuarioEncontrado);

        return usuarioMapper.toDto(usuarioEncontrado);
    }
}
