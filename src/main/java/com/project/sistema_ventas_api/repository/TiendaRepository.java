package com.project.sistema_ventas_api.repository;

import com.project.sistema_ventas_api.entity.Tienda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TiendaRepository extends JpaRepository<Tienda, String> {
}
