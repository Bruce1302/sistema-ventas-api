package com.project.sistema_ventas_api.entity;

import com.project.sistema_ventas_api.enums.EstadoSunat;
import com.project.sistema_ventas_api.enums.TipoComprobante;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", length = 20)
    private TipoComprobante tipoComprobante = TipoComprobante.NOTA_VENTA;

    @Column(name = "serie_numero", length = 20)
    private String serieNumero; // "FA01-1234" o "BA01-35"

    @Column(name = "external_id", length = 100)
    private String externalId; // El UUID que devuelve FactuSmart para descargar PDF/XML/CDR

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_sunat", length = 20)
    private EstadoSunat estadoSunat = EstadoSunat.INTERNO;

    @Column(name = "cliente_tipo_doc", length = 2)
    private String clienteTipoDoc;

    @Column(name = "cliente_num_doc", length = 15)
    private String clienteNumDoc;

    @Column(name = "cliente_nombre")
    private String clienteNombre;

}


