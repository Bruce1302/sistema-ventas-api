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

    @Column(name = "facturacion_activa")
    private Boolean facturacionActiva = false;

    @Column(length = 11)
    private String ruc;

    @Column(name = "razon_social")
    private String razonSocial;

    @Column(name = "direccion_fiscal")
    private String direccionFiscal;

    @Column(name = "token_facturacion")
    private String tokenFacturacion;

    @Column(name = "serie_boleta", length = 4)
    private String serieBoleta;

    @Column(name = "serie_factura", length = 4)
    private String serieFactura;


}
