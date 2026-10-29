package com.project.sistema_ventas_api.repository;

import com.project.sistema_ventas_api.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, String> {
    Page<Producto> findAllByActivo(Boolean activo, Pageable pageable);

    //top productos mas vendidos
    @Query("SELECT d.producto FROM DetalleVenta d GROUP BY d.producto ORDER BY SUM(d.cantidad) DESC")
    List<Producto> findTopProductosMasVendidos(Pageable pageable);

    Page<Producto> findByNombreContainingIgnoreCaseAndActivo(String nombre, Boolean estado, Pageable pageable);

    //filtros por categoria
    Page<Producto> findByCategoria_IdIn(Collection<String> categoriaIds, Pageable pageable);
}
