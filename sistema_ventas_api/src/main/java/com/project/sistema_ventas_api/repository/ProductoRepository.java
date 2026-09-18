package com.project.sistema_ventas_api.repository;

import com.project.sistema_ventas_api.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, String> {
}
