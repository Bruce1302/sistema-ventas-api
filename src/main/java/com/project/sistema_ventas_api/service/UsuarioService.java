package com.project.sistema_ventas_api.service;

import com.project.sistema_ventas_api.dto.usuarioDTO.UsuarioRequestDTO;
import com.project.sistema_ventas_api.dto.usuarioDTO.UsuarioResponseDTO;
import com.project.sistema_ventas_api.entity.Tienda;
import com.project.sistema_ventas_api.entity.Usuario;
import com.project.sistema_ventas_api.exception.RecursoNoEncontradoException;
import com.project.sistema_ventas_api.mapper.UsuarioMapper;
import com.project.sistema_ventas_api.repository.TiendaRepository;
import com.project.sistema_ventas_api.repository.UsuarioRepository;
import com.project.sistema_ventas_api.security.TenantContext;
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
    private final TiendaRepository tiendaRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder, TiendaRepository tiendaRepository)
    {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.tiendaRepository = tiendaRepository;
    }

    @Transactional
    public UsuarioResponseDTO nuevoUsuario(UsuarioRequestDTO requestDTO)
    {
        String tiendaIdActual = TenantContext.getCurrentTenant();

        // Validamos manualmente que el pasword tenga contenido
        if (requestDTO.getPassword() == null || requestDTO.getPassword().trim().isEmpty())
            throw new IllegalArgumentException("La contraseña es obligatoria para usuarios nuevos");

        //Buscamos el username para no duplicarlo
        if (usuarioRepository.findByUsernameAndTienda_Id(requestDTO.getUsername(), tiendaIdActual).isPresent())
            throw new IllegalArgumentException("El username '" + requestDTO.getUsername() + "' ya está en uso. Elige otro.");

        //obtenemos el id de la tienda
        Tienda tiendaRef = tiendaRepository.getReferenceById(tiendaIdActual);

        Usuario usuario = usuarioMapper.toEntity(requestDTO);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword())); //Encriptamos la contraseña antes de guardar
        usuario.setTienda(tiendaRef);

        return usuarioMapper.toDto(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarUsuarios()
    {
        String tiendaIdActual = TenantContext.getCurrentTenant();

        return  usuarioMapper.toDtoList(usuarioRepository.findAllByTienda_Id(tiendaIdActual));
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarUsuarioPorId(UUID id)
    {
        return usuarioMapper.toDto(usuarioRepository.findById(id.toString()).orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado")));
    }

    @Transactional
    public void eliminarUsuario(UUID id)
    {
        String tiendaIdActual = TenantContext.getCurrentTenant();

        Usuario usuarioEncontrado = usuarioRepository.findByIdAndTienda_Id(id.toString(), tiendaIdActual).orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        usuarioEncontrado.setActivo(false);
        usuarioRepository.save(usuarioEncontrado);
    }

    @Transactional
    public void activarUsuario(UUID id)
    {
        String tiendaIdActual = TenantContext.getCurrentTenant();

        Usuario usuarioEncontrado =  usuarioRepository.findByIdAndTienda_Id(id.toString(), tiendaIdActual).orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        usuarioEncontrado.setActivo(true);
        usuarioRepository.save(usuarioEncontrado);
    }

    @Transactional
    public UsuarioResponseDTO actualizarUsuario(UsuarioRequestDTO requestDTO, UUID id)
    {
        String tiendaIdActual = TenantContext.getCurrentTenant();

        Usuario usuarioEncontrado = usuarioRepository.findByIdAndTienda_Id(id.toString(), tiendaIdActual).orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        //actualizaos los datos
        usuarioEncontrado.setUsername(requestDTO.getUsername());
        usuarioEncontrado.setRol(requestDTO.getRol());

        //si es que se ingresa una contraseña tambien se actualiza, pero si esta vacia, solo se cambian los campos llenos
        if (requestDTO.getPassword() != null && !requestDTO.getPassword().trim().isEmpty())
            usuarioEncontrado.setPassword(passwordEncoder.encode(requestDTO.getPassword()));


        usuarioRepository.save(usuarioEncontrado);

        return usuarioMapper.toDto(usuarioEncontrado);
    }

    @Transactional
    public void destruirUsuario(UUID id)
    {
        String tiendaIdActual = TenantContext.getCurrentTenant();

        //validamos si el usuario existe
        Usuario usuario = usuarioRepository.findByIdAndTienda_Id(id.toString(), tiendaIdActual).orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        //eliminamos
        usuarioRepository.delete(usuario);
    }
}
