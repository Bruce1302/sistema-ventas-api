package com.project.sistema_ventas_api.dto.tiendaDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TiendaRequestDTO {

    @NotBlank
    private String codigo;

    @NotBlank
    private String nombre;

    @NotBlank
    private String direccion;

    private String telefono;

    private Boolean facturacionActiva;

    private String ruc;

    private String razonSocial;

    private String direccionFiscal;

    private String tokenFacturacion;

    private String serieBoleta;

    private String serieFactura;

}
