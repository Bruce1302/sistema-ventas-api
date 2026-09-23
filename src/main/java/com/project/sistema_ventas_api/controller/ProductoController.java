package com.project.sistema_ventas_api.controller;

import com.project.sistema_ventas_api.dto.productoDTO.ProductoRequestDTO;
import com.project.sistema_ventas_api.dto.productoDTO.ProductoResponseDTO;
import com.project.sistema_ventas_api.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService)
    {
        this.productoService = productoService;
    }

    @PostMapping()
    public ProductoResponseDTO nuevoProducto( @Valid @RequestBody ProductoRequestDTO requestDTO)
    {
        return productoService.nuevoProducto(requestDTO);
    }

    @GetMapping()
    public Page<ProductoResponseDTO> listarProductos(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size)
    {
        return productoService.listarProductos(page, size);
    }

    @DeleteMapping("/{id}")
    public void eliminarProducto(@PathVariable UUID id)
    {
        productoService.eliminarProducto(id);
    }

    @PutMapping("/{id}")
    public ProductoResponseDTO actualizarProducto( @Valid @RequestBody ProductoRequestDTO requestDTO, @PathVariable UUID id)
    {
        return productoService.actualizarProducto(id, requestDTO);
    }
}
