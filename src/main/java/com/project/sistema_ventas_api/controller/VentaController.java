package com.project.sistema_ventas_api.controller;

import com.project.sistema_ventas_api.dto.ventaDTO.VentaRequestDTO;
import com.project.sistema_ventas_api.dto.ventaDTO.VentaResponseDTO;
import com.project.sistema_ventas_api.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService)
    {
        this.ventaService = ventaService;
    }

    @PostMapping()
    public VentaResponseDTO nuevaVenta(@Valid @RequestBody VentaRequestDTO requestDTO, @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey)
    {
        return ventaService.registrarVenta(requestDTO, idempotencyKey);
    }

    @GetMapping()
    public Page<VentaResponseDTO> listarVentas(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size)
    {
        return ventaService.listarVentas(page, size);
    }

    @DeleteMapping("/{id}")
    public void eliminarVenta(@PathVariable UUID id)
    {
        ventaService.eliminarVenta(id);
    }

    @PutMapping("/{id}")
    public VentaResponseDTO actualizarVenta(@Valid @RequestBody VentaRequestDTO requestDTO, @PathVariable UUID id)
    {
        return ventaService.actualizarVenta(id, requestDTO);
    }

    @GetMapping("/{id}")
    public VentaResponseDTO buscarVentaPorId(@PathVariable UUID id)
    {
        return ventaService.buscarVentaPorId(id);
    }

    @GetMapping("/filtrar")
    public Page<VentaResponseDTO> filtrarVentasPorFecha(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
                                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size)
    {
        return ventaService.filtrarVentasPorFecha(fechaInicio, fechaFin, page, size);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> emitirPdf(@PathVariable UUID id, @RequestParam(required = false) String formato)
    {
        byte[] pdfBytes = ventaService.obtenerPdfComprobante(id, formato);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"comprobante-" + id + ".pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes);
    }

    @PostMapping("/{id}/reenviar")
    public VentaResponseDTO reenviarSunat(@PathVariable String id)
    {
        return ventaService.reenviarVentaSunat(id);
    }
}
