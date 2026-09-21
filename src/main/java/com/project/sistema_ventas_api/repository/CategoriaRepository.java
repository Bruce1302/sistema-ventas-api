package com.project.sistema_ventas_api.repository;

import com.project.sistema_ventas_api.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, String> {

    List<Categoria> findAllByActivo(Boolean activo);
}
