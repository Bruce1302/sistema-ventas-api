package com.project.sistema_ventas_api.repository;

import com.project.sistema_ventas_api.entity.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface VentaRepository extends JpaRepository<Venta, String> {

    Page<Venta> findAllByActivo(boolean b, Pageable pageable);

    //Para los filtros
    Page<Venta> findByFechaCreacionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);
}
