package com.project.sistema_ventas_api.repository;

import com.project.sistema_ventas_api.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, String> {
    Page<Producto> findAllByActivo(Boolean activo, Pageable pageable);

    //top productos mas vendidos
    @Query("SELECT d.producto FROM DetalleVenta d GROUP BY d.producto ORDER BY SUM(d.cantidad) DESC")
    List<Producto> findTopProductosMasVendidos(Pageable pageable);

    Page<Producto> findByNombreContainingIgnoreCaseAndActivo(String nombre, Boolean estado, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND (LOWER(p.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR p.codigo LIKE CONCAT('%', :termino, '%'))")
    Page<Producto> buscarActivosPorNombreOCodigo(@Param("termino") String termino, Pageable pageable);

    Page<Producto> findByCategoria_IdInAndActivo(Collection<String> categoriaIds, Boolean activo, Pageable pageable);

    Optional<Producto> findFirstByCodigoOrderByActivoDesc(String codigo);
}
