package com.project.sistema_ventas_api.service;

import com.project.sistema_ventas_api.dto.productoDTO.ProductoRequestDTO;
import com.project.sistema_ventas_api.dto.productoDTO.ProductoResponseDTO;
import com.project.sistema_ventas_api.entity.Categoria;
import com.project.sistema_ventas_api.entity.Producto;
import com.project.sistema_ventas_api.mapper.ProductoMapper;
import com.project.sistema_ventas_api.repository.CategoriaRepository;
import com.project.sistema_ventas_api.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, ProductoMapper productoMapper, CategoriaRepository categoriaRepository)
    {
        this.productoRepository = productoRepository;
        this.productoMapper = productoMapper;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public ProductoResponseDTO nuevoProducto(ProductoRequestDTO requestDTO)
    {
        Categoria categoriaEncontrada = categoriaRepository.findById(requestDTO.getCategoriaId().toString()).orElseThrow(() -> new RuntimeException("Categoria no encontrada"));

        Producto nuevoProducto = productoMapper.toEntity(requestDTO);
        nuevoProducto.setCategoria(categoriaEncontrada);

        return productoMapper.toDto(productoRepository.save(nuevoProducto));
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarProductos()
    {
        return  productoMapper.toDtoList(productoRepository.findAll());
    }

    @Transactional
    public void eliminarProducto(UUID id)
    {
        productoRepository.findById(id.toString()).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        productoRepository.deleteById(id.toString());
    }

    @Transactional
    public ProductoResponseDTO actualizarProducto(UUID id, ProductoRequestDTO requestDTO)
    {
        Producto productoEncontrado = productoRepository.findById(id.toString()).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Categoria categoriaEncontrada = categoriaRepository.findById(requestDTO.getCategoriaId().toString()).orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
        productoEncontrado.setNombre(requestDTO.getNombre());
        productoEncontrado.setPrecio(requestDTO.getPrecio());
        productoEncontrado.setStock(requestDTO.getStock());
        productoEncontrado.setCategoria(categoriaEncontrada);

        return productoMapper.toDto(productoRepository.save(productoEncontrado));

    }



}

