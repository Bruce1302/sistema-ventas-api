package com.project.sistema_ventas_api.controller;

import com.project.sistema_ventas_api.dto.categoriaDTO.CategoriaRequestDTO;
import com.project.sistema_ventas_api.dto.categoriaDTO.CategoriaResponseDTO;
import com.project.sistema_ventas_api.service.CategoriaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService)
    {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public CategoriaResponseDTO nuevaCategoria(@RequestBody CategoriaRequestDTO requestDTO)
    {
       return categoriaService.nuevaCategoria(requestDTO);
    }

    @GetMapping
    public List<CategoriaResponseDTO> listarCategorias()
    {
        return categoriaService.listarCategorias();
    }

    @DeleteMapping("/{id}/eliminarCategoria")
    public void eliminarCategoria(@PathVariable UUID id)
    {
        categoriaService.eliminarCategoria(id);
    }

    @PutMapping("/{id}")
    public CategoriaResponseDTO actualizarCategoria(@RequestBody CategoriaRequestDTO requestDTO, @PathVariable UUID id)
    {
        return categoriaService.actualizarCategoria(requestDTO, id);
    }
}
