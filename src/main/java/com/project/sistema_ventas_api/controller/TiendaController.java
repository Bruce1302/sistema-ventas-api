package com.project.sistema_ventas_api.controller;

import com.project.sistema_ventas_api.dto.tiendaDTO.TiendaRequestDTO;
import com.project.sistema_ventas_api.dto.tiendaDTO.TiendaResponseDTO;
import com.project.sistema_ventas_api.service.TiendaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tiendas")
public class TiendaController {

    private final TiendaService tiendaService;

    public TiendaController(TiendaService tiendaService)
    {
        this.tiendaService = tiendaService;
    }

    @PostMapping
    public TiendaResponseDTO nuevaTienda(TiendaRequestDTO requestDTO)
    {
        return tiendaService.nuevaTienda(requestDTO);
    }

    @GetMapping("/{id}")
    public TiendaResponseDTO llamarTienda( @PathVariable String id)
    {
        return tiendaService.llamarTienda(id);
    }

    @DeleteMapping
    public void eliminarTienda(@PathVariable UUID id)
    {
        tiendaService.eliminarTienda(id);
    }


}
