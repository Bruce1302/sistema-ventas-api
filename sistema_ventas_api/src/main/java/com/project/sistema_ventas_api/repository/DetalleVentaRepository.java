package com.project.sistema_ventas_api.repository;

import com.project.sistema_ventas_api.entity.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, String> {
}
