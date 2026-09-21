package com.project.sistema_ventas_api.controller;

import com.project.sistema_ventas_api.dto.ventaDTO.VentaRequestDTO;
import com.project.sistema_ventas_api.dto.ventaDTO.VentaResponseDTO;
import com.project.sistema_ventas_api.service.VentaService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

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
    public void nuevaVenta(@RequestBody VentaRequestDTO requestDTO)
    {
        ventaService.registrarVenta(requestDTO);
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
    public VentaResponseDTO actualizarVenta(@RequestBody VentaRequestDTO requestDTO, @PathVariable UUID id)
    {
        return ventaService.actualizarVenta(id, requestDTO);
    }

    @GetMapping("/{id}")
    public VentaResponseDTO buscarVentaPorId(@PathVariable UUID id)
    {
        return ventaService.buscarVentaPorId(id);
    }
}
