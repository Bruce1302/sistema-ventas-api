package com.project.sistema_ventas_api.service;

import com.project.sistema_ventas_api.dto.tiendaDTO.TiendaRequestDTO;
import com.project.sistema_ventas_api.dto.tiendaDTO.TiendaResponseDTO;
import com.project.sistema_ventas_api.entity.Tienda;
import com.project.sistema_ventas_api.mapper.TiendaMapper;
import com.project.sistema_ventas_api.repository.TiendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TiendaService {

    private final TiendaRepository tiendaRepository;

    private final TiendaMapper tiendaMapper;
    public TiendaService(TiendaRepository tiendaRepository, TiendaMapper tiendaMapper)
    {
        this.tiendaRepository = tiendaRepository;
        this.tiendaMapper = tiendaMapper;
    }

    @Transactional
    public TiendaResponseDTO nuevaTienda(TiendaRequestDTO requestDTO)
    {
        return tiendaMapper.toDto(tiendaRepository.save(tiendaMapper.toEntity(requestDTO)));
    }

    @Transactional(readOnly = true)
    public List<TiendaResponseDTO> listarTiendas()
    {
        return tiendaMapper.toDtoList(tiendaRepository.findAll());
    }

    @Transactional
    public void eliminarTienda(UUID id)
    {
        Tienda tiendaEncontrada = tiendaRepository.findById(id.toString()).orElseThrow(() -> new RuntimeException("Tienda no encontrada"));

        tiendaEncontrada.setActivo(false);
    }

    @Transactional
    public TiendaResponseDTO actualizarTienda(TiendaRequestDTO requestDTO, UUID id)
    {
        Tienda tiendaEncontrada = tiendaRepository.findById(id.toString()).orElseThrow(() -> new RuntimeException("Tienda no encontrada"));

        tiendaEncontrada.setNombre(requestDTO.getNombre());
        tiendaEncontrada.setCodigo(requestDTO.getCodigo());
        tiendaEncontrada.setDireccion(requestDTO.getDireccion());
        tiendaEncontrada.setTelefono(requestDTO.getTelefono());

        return tiendaMapper.toDto(tiendaRepository.save(tiendaEncontrada));
    }
}
