package com.project.sistema_ventas_api.service;

import com.project.sistema_ventas_api.entity.DetalleVenta;
import com.project.sistema_ventas_api.entity.Tienda;
import com.project.sistema_ventas_api.entity.Venta;
import com.project.sistema_ventas_api.enums.EstadoSunat;
import com.project.sistema_ventas_api.enums.TipoComprobante;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.core.ParameterizedTypeReference;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FacturacionService {

    private final RestClient restClient;

    public FacturacionService(@Value("${factusmart.api.url}") String apiUrl)
    {
        this.restClient = RestClient.builder().baseUrl(apiUrl).build();
    }

    public void emitirComprobante(Venta venta, Tienda tienda) {

        // Si la tienda tiene facturación apagada o el cliente pidio Nota de Venta, no generamos consulta, sera una venta simple
        if (!Boolean.TRUE.equals(tienda.getFacturacionActiva()) || venta.getTipoComprobante() == null || venta.getTipoComprobante() == TipoComprobante.NOTA_VENTA)
        {
            venta.setEstadoSunat(EstadoSunat.INTERNO);
            return;
        }

        // Validar reglas basicas antes de enviar a FactuSmart
        if (venta.getTipoComprobante() == TipoComprobante.FACTURA
                && (!"6".equals(venta.getClienteTipoDoc()) || venta.getClienteNumDoc() == null || venta.getClienteNumDoc().isBlank()))
        {
            throw new IllegalArgumentException("Para emitir Factura es obligatorio el RUC del cliente.");
        }

        if (venta.getClienteNumDoc() != null && !venta.getClienteNumDoc().isBlank()
                && !"1".equals(venta.getClienteTipoDoc()) && !"6".equals(venta.getClienteTipoDoc())
                && (venta.getClienteNombre() == null || venta.getClienteNombre().isBlank()))
        {
            throw new IllegalArgumentException("Para Carné de Extranjería u otros documentos debes ingresar el nombre del cliente.");
        }

        //Si los datos estan correctos entonces procesamos la factura
        try {
            String apiKey = tienda.getTokenFacturacion();

            //  usamos el endpoint /calcular para obtener bases e IGV exactos sin errores de redondeo
            List<Map<String,Object>> itemsCalcular = venta.getDetalles().stream()
                    .map(this::mapearItemCalcular)
                    .toList();

            Map<String, Object> respuestaCalcular = restClient.post()
                    .uri("/calcular")
                    .header("X-API-Key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("items", itemsCalcular))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            // Armamos fecha/hora oficial
            ZonedDateTime ahoraLima = ZonedDateTime.now(ZoneId.of("America/Lima"));
            String fechaEmision = ahoraLima.format(DateTimeFormatter.ISO_LOCAL_DATE); // YYYY-MM-DD
            String horaEmision = ahoraLima.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            // "01" = Factura, "03" = Boleta
            String codigoTipoDoc = (venta.getTipoComprobante() == TipoComprobante.FACTURA) ? "01" : "03";

            // Armar datos del cliente
            Map<String, Object> cliente = new HashMap<>();
            if (venta.getClienteNumDoc() != null && !venta.getClienteNumDoc().isBlank())
            {
                cliente.put("codigo_tipo_documento_identidad", venta.getClienteTipoDoc()); // "1" DNI, "6" RUC
                cliente.put("numero_documento", venta.getClienteNumDoc());

                if (venta.getClienteNombre() != null && !venta.getClienteNombre().isBlank())
                    cliente.put("apellidos_y_nombres_o_razon_social", venta.getClienteNombre());

            }
            else
            {
                // Boleta menor a S/ 700 sin documento se usa "Clientes Varios"
                cliente.put("codigo_tipo_documento_identidad", "0");
                cliente.put("numero_documento", "00000000");
                cliente.put("apellidos_y_nombres_o_razon_social", "CLIENTES VARIOS");
            }

            // Armar cuerpo para POST /documents
            Map<String, Object> bodyDocumento = new HashMap<>();
            bodyDocumento.put("ruc", tienda.getRuc());
            bodyDocumento.put("numero_documento", "#"); // FactuSmart asigna serie y correlativo automáticamente
            bodyDocumento.put("fecha_de_emision", fechaEmision);
            bodyDocumento.put("hora_de_emision", horaEmision);
            bodyDocumento.put("codigo_tipo_operacion", "0101"); // Venta interna
            bodyDocumento.put("codigo_tipo_documento", codigoTipoDoc);
            bodyDocumento.put("codigo_tipo_moneda", "PEN");
            bodyDocumento.put("datos_del_cliente_o_receptor", cliente);
            bodyDocumento.put("items", respuestaCalcular.get("items"));
            bodyDocumento.put("totales", respuestaCalcular.get("totales"));

            // Enviar comprobante usando el ID de la Venta como Idempotency-Key
            Map<String, Object> respuestaDoc = restClient.post()
                    .uri("/documents")
                    .header("X-API-Key", apiKey)
                    .header("Idempotency-Key", tienda.getRuc() + "-venta-" + venta.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(bodyDocumento)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            // Guardar datos oficiales en la Venta
            if (respuestaDoc != null)
            {
                venta.setSerieNumero((String) respuestaDoc.get("serie_numero"));
                venta.setExternalId((String) respuestaDoc.get("external_id"));

                Boolean aceptado = (Boolean) respuestaDoc.get("aceptado_por_sunat");
                String estadoCodigo = (String) respuestaDoc.get("estado");

                if (Boolean.TRUE.equals(aceptado) || "05".equals(estadoCodigo))
                    venta.setEstadoSunat(EstadoSunat.ACEPTADO);
                else if ("09".equals(estadoCodigo))
                    venta.setEstadoSunat(EstadoSunat.RECHAZADO);
                else
                    venta.setEstadoSunat(EstadoSunat.PENDIENTE);

            }

        } catch (Exception e) {
            // Si FactuSmart o SUNAT fallan, NO rompemos la venta interna: queda PENDIENTE y se reencia luego
            venta.setEstadoSunat(EstadoSunat.PENDIENTE);
            System.err.println("Error al emitir comprobante en FactuSmart: " + e.getMessage());
        }
    }

    public byte[] descargarPdf(String externalId, Tienda tienda, String formato)
    {
        return restClient.get()
            .uri(uriBuilder -> {
                uriBuilder.path("/documents/{externalId}/pdf")
                        .queryParam("ruc", tienda.getRuc());
                if (formato != null && !formato.isBlank()) {
                    uriBuilder.queryParam("formato", formato);
                }
                return uriBuilder.build(externalId);
            })
            .header("X-API-Key", tienda.getTokenFacturacion())
            .retrieve()
            .body(byte[].class);
    }

    private Map<String, Object> mapearItemCalcular(DetalleVenta detalle)
    {
        Map<String, Object> item = new HashMap<>();
        item.put("codigo_interno", detalle.getProducto().getCodigo() != null
                ? detalle.getProducto().getCodigo()
                : detalle.getProducto().getId());
        item.put("descripcion", detalle.getProducto().getNombre());
        item.put("cantidad", detalle.getCantidad());
        item.put("precio_unitario", detalle.getPrecioUnitario()); // Precio final con IGV incluido
        item.put("afectacion", "10"); // Gravado - Operación Onerosa (18% IGV)
        item.put("unidad_de_medida", "NIU"); // Bienes / Unidades
        return item;
    }

}
