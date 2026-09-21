package com.project.sistema_ventas_api.service;

import com.project.sistema_ventas_api.dto.productoDTO.ProductoRequestDTO;
import com.project.sistema_ventas_api.dto.productoDTO.ProductoResponseDTO;
import com.project.sistema_ventas_api.entity.Categoria;
import com.project.sistema_ventas_api.entity.Producto;
import com.project.sistema_ventas_api.exception.RecursoNoEncontradoException;
import com.project.sistema_ventas_api.mapper.ProductoMapper;
import com.project.sistema_ventas_api.repository.CategoriaRepository;
import com.project.sistema_ventas_api.repository.ProductoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        Categoria categoriaEncontrada = categoriaRepository.findById(requestDTO.getCategoriaId().toString()).orElseThrow(() -> new RecursoNoEncontradoException("Categoria no encontrada"));

        Producto nuevoProducto = productoMapper.toEntity(requestDTO);
        nuevoProducto.setCategoria(categoriaEncontrada);
d 
        return productoMapper.toDto(productoRepository.save(nuevoProducto));
    }

    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> listarProductos(int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size);

        Page<Producto> paginaProductos = productoRepository.findAllByActivo(true, pageable);

        return paginaProductos.map(producto -> productoMapper.toDto(producto));
    }

    @Transactional
    public void eliminarProducto(UUID id)
    {
        Producto productoEncontrado = productoRepository.findById(id.toString()).orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
        //productoRepository.deleteById(id.toString());

        productoEncontrado.setActivo(false);
    }

    @Transactional
    public ProductoResponseDTO actualizarProducto(UUID id, ProductoRequestDTO requestDTO)
    {
        Producto productoEncontrado = productoRepository.findById(id.toString()).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Categoria categoriaEncontrada = categoriaRepository.findById(requestDTO.getCategoriaId().toString()).orElseThrow(() -> new RecursoNoEncontradoException("Categoria no encontrada"));
        productoEncontrado.setNombre(requestDTO.getNombre());
        productoEncontrado.setPrecio(requestDTO.getPrecio());
        productoEncontrado.setStock(requestDTO.getStock());
        productoEncontrado.setCategoria(categoriaEncontrada);

        return productoMapper.toDto(productoRepository.save(productoEncontrado));

    }



}

