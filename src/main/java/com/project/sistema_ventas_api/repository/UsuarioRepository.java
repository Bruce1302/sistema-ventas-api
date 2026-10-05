package com.project.sistema_ventas_api.repository;

import com.project.sistema_ventas_api.entity.Usuario;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    List<Usuario> findAllByActivo(boolean b);

    Optional<Usuario> findByUsernameAndActivo(String username, Boolean activo);

    Optional<Object> findByUsername(@NotBlank String username);
}
