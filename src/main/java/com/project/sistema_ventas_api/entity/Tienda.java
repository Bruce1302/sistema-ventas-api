package com.project.sistema_ventas_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.TenantId;

@Data
@Entity
public class Tienda extends AuditoriaBase{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank
    @Column(unique = true)
    private String codigo;

    @NotBlank
    private String nombre;

    @NotBlank
    private String direccion;

    private String telefono;

}
