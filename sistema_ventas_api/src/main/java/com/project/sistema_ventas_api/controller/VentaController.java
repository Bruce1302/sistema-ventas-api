package com.project.sistema_ventas_api.controller;

import com.project.sistema_ventas_api.dto.ventaDTO.VentaRequestDTO;
import com.project.sistema_ventas_api.dto.ventaDTO.VentaResponseDTO;
import com.project.sistema_ventas_api.service.VentaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<VentaResponseDTO> listarVentas()
    {
        return null;
    }
}
