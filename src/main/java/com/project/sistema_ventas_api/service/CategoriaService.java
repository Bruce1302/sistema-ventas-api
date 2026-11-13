package com.project.sistema_ventas_api.service;

import com.project.sistema_ventas_api.dto.categoriaDTO.CategoriaRequestDTO;
import com.project.sistema_ventas_api.dto.categoriaDTO.CategoriaResponseDTO;
import com.project.sistema_ventas_api.entity.Categoria;
import com.project.sistema_ventas_api.entity.Tienda;
import com.project.sistema_ventas_api.exception.RecursoNoEncontradoException;
import com.project.sistema_ventas_api.mapper.CategoriaMapper;
import com.project.sistema_ventas_api.repository.CategoriaRepository;
import com.project.sistema_ventas_api.repository.TiendaRepository;
import com.project.sistema_ventas_api.security.TenantContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;
    private final TiendaRepository tiendaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper, TiendaRepository tiendaRepository)
    {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
        this.tiendaRepository = tiendaRepository;
    }

    @Transactional
    public CategoriaResponseDTO nuevaCategoria(CategoriaRequestDTO requestDTO)
    {
        //Obtenemos el id de la tienda
        String tiendaIdActual = TenantContext.getCurrentTenant();
        Tienda tiendaRef = tiendaRepository.getReferenceById(tiendaIdActual);

        Categoria categoria = categoriaMapper.toEntity(requestDTO);

        categoria.setTienda(tiendaRef);

        return categoriaMapper.toDto(categoriaRepository.save(categoria));
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarCategorias()
    {
        return categoriaMapper.toDtoList(categoriaRepository.findAllByActivo(true)); //Solo los que tengan el estado activo
    }

    @Transactional
    public void eliminarCategoria(UUID id)
    {
        Categoria categoriaEntontrada = categoriaRepository.findById(id.toString()).orElseThrow(() -> new RecursoNoEncontradoException("Categoria no encontrada"));

        //categoriaRepository.deleteById(id.toString()); -- Borrado Fisico

        categoriaEntontrada.setActivo(false); //Borrado logico

    }

    @Transactional
    public CategoriaResponseDTO actualizarCategoria(CategoriaRequestDTO requestDTO, UUID id)
    {
        Categoria categoriaEncontrada = categoriaRepository.findById(id.toString()).orElseThrow(() -> new RecursoNoEncontradoException("Categoria no encontrada"));

        categoriaEncontrada.setNombre(requestDTO.getNombre());

        return categoriaMapper.toDto(categoriaRepository.save(categoriaEncontrada));
    }
}
