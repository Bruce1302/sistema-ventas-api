package com.project.sistema_ventas_api.repository;

import com.project.sistema_ventas_api.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, String> {


}
