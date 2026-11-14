package com.project.sistema_ventas_api.repository;

import com.project.sistema_ventas_api.entity.Usuario;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    Optional<Usuario> findByUsernameAndTienda_Id(@NotBlank String username, @NotBlank String tiendaId);

    List<Usuario> findAllByTienda_Id(String tiendaId);

    Optional<Usuario> findByIdAndTienda_Id(String id, String tiendaId);

    Optional<Usuario> findByUsernameAndTienda_CodigoAndActivo(String username, String codigoTienda, boolean activo);

    Optional<Usuario> findByUsernameAndTienda_IdAndActivo(String username, String tiendaId, Boolean activo);

}
