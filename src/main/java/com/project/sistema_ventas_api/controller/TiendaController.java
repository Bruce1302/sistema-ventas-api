package com.project.sistema_ventas_api.controller;

import com.project.sistema_ventas_api.dto.tiendaDTO.TiendaRequestDTO;
import com.project.sistema_ventas_api.dto.tiendaDTO.TiendaResponseDTO;
import com.project.sistema_ventas_api.service.TiendaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tienda")
public class TiendaController {

    private final TiendaService tiendaService;

    public TiendaController(TiendaService tiendaService)
    {
        this.tiendaService = tiendaService;
    }

    @PostMapping
    public TiendaResponseDTO nuevaTienda(TiendaRequestDTO requestDTO)
    {
        tiendaService.nuevaTienda(requestDTO);
    }

    @GetMapping
    public List<TiendaResponseDTO> listarTiendas()
    {
        return tiendaService.listarTiendas();
    }


}
