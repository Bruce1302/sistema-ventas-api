package com.project.sistema_ventas_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.hibernate.annotations.TenantId;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
public class Venta extends AuditoriaBase{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    //Cantidad de productos vendidos

    @NotNull
    @PositiveOrZero
    private BigDecimal total;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    private List<DetalleVenta> detalles;

    @ManyToOne
    @JoinColumn(name = "tienda_id")
    private Tienda tienda;

    @TenantId
    @Column(name = "tienda_id", insertable = false, updatable = false)
    private String tenantId;

}


