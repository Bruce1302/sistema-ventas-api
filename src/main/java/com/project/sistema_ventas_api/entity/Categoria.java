package com.project.sistema_ventas_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.TenantId;

@Data
@Entity
public class Categoria extends AuditoriaBase{ //Contendra los campos de AuditoriaBase

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "tienda_id")
    private Tienda tienda;

    @TenantId
    @Column(name = "tienda_id", insertable = false, updatable = false)
    private String tenantId;


}
