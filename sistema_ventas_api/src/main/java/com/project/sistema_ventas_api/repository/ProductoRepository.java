package com.project.sistema_ventas_api.repository;

import com.project.sistema_ventas_api.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, String> {
    Page<Producto> findAllByActivo(Boolean activo, Pageable pageable);
}
