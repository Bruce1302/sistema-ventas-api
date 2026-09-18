package com.project.sistema_ventas_api.repository;

import com.project.sistema_ventas_api.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, String> {
}
