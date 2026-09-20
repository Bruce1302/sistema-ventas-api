package com.project.sistema_ventas_api.service;

import com.project.sistema_ventas_api.dto.categoriaDTO.CategoriaRequestDTO;
import com.project.sistema_ventas_api.dto.categoriaDTO.CategoriaResponseDTO;
import com.project.sistema_ventas_api.entity.Categoria;
import com.project.sistema_ventas_api.mapper.CategoriaMapper;
import com.project.sistema_ventas_api.repository.CategoriaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;
    public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper)
    {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    @Transactional
    public CategoriaResponseDTO nuevaCategoria(CategoriaRequestDTO requestDTO)
    {
        return categoriaMapper.toDto(categoriaRepository.save(categoriaMapper.toEntity(requestDTO)));
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarCategorias()
    {
        return categoriaMapper.toDtoList(categoriaRepository.findAll());
    }

    @Transactional
    public void eliminarCategoria(UUID id)
    {
        categoriaRepository.findById(id.toString()).orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
        categoriaRepository.deleteById(id.toString());
    }

    @Transactional
    public CategoriaResponseDTO actualizarCategoria(CategoriaRequestDTO requestDTO, UUID id)
    {
        Categoria categoriaEncontrada = categoriaRepository.findById(id.toString()).orElseThrow(() -> new RuntimeException("Categoria no encontrada"));

        categoriaEncontrada.setNombre(requestDTO.getNombre());

        return categoriaMapper.toDto(categoriaRepository.save(categoriaEncontrada));
    }
}
