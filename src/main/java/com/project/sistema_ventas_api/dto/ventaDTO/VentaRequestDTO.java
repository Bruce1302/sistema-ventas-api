package com.project.sistema_ventas_api.dto.ventaDTO;

import com.project.sistema_ventas_api.dto.detalleVentaDTO.DetalleVentaRequestDTO;
import com.project.sistema_ventas_api.entity.DetalleVenta;
import com.project.sistema_ventas_api.enums.TipoComprobante;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class VentaRequestDTO {

    @NotNull
    private List<DetalleVentaRequestDTO> detalles;

    private TipoComprobante tipoComprobante; // Si viene null desde Angular, en el Service asumimos NOTA_VENTA
    private String clienteTipoDoc;           // "1" (DNI) o "6" (RUC)
    private String clienteNumDoc;
    private String clienteNombre;

}
