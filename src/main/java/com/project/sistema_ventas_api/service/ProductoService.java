package com.project.sistema_ventas_api.service;

import com.project.sistema_ventas_api.dto.productoDTO.ProductoRequestDTO;
import com.project.sistema_ventas_api.dto.productoDTO.ProductoResponseDTO;
import com.project.sistema_ventas_api.entity.Categoria;
import com.project.sistema_ventas_api.entity.Producto;
import com.project.sistema_ventas_api.entity.Tienda;
import com.project.sistema_ventas_api.exception.RecursoNoEncontradoException;
import com.project.sistema_ventas_api.mapper.ProductoMapper;
import com.project.sistema_ventas_api.repository.CategoriaRepository;
import com.project.sistema_ventas_api.repository.ProductoRepository;
import com.project.sistema_ventas_api.repository.TiendaRepository;
import com.project.sistema_ventas_api.security.TenantContext;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final CategoriaRepository categoriaRepository;
    private final TiendaRepository tiendaRepository;

    public ProductoService(ProductoRepository productoRepository, ProductoMapper productoMapper, CategoriaRepository categoriaRepository, TiendaRepository tiendaRepository)
    {
        this.productoRepository = productoRepository;
        this.productoMapper = productoMapper;
        this.categoriaRepository = categoriaRepository;
        this.tiendaRepository = tiendaRepository;
    }

    @Transactional
    public ProductoResponseDTO nuevoProducto(ProductoRequestDTO requestDTO)
    {
        Categoria categoriaEncontrada = categoriaRepository.findById(requestDTO.getCategoriaId()).orElseThrow(() -> new RecursoNoEncontradoException("Categoria no encontrada"));

        //Obtenemos el id de la tienda
        String tiendaIdActual = TenantContext.getCurrentTenant();

        Tienda tiendaRef = tiendaRepository.getReferenceById(tiendaIdActual);

        //Antes de agregar, buscamos si ya existe el codigo de barras del producto
        Optional<Producto> productoExistente = productoRepository.findFirstByCodigoOrderByActivoDesc(requestDTO.getCodigo());

        //si es que existe
        if(productoExistente.isPresent())
        {
            Producto producto = productoExistente.get();

            System.out.println( "el producto esta " + producto.getActivo());
            //si el codigo de barras ya esta registrado
            if (producto.getActivo())
                throw new IllegalArgumentException("Ya existe un producto con el código de barras '" + requestDTO.getCodigo() + "'.");

            //Si el producto esta inactivo, significa que se elimino de forma logica, lo activamos y actualizamos datos
            producto.setActivo(true);
            producto.setNombre(requestDTO.getNombre());
            producto.setPrecio(requestDTO.getPrecio());
            producto.setStock(requestDTO.getStock());
            producto.setCategoria(categoriaEncontrada);

            return productoMapper.toDto(productoRepository.save(producto));
        }

        //si el producto no existe, se ingresa de forma normal
        Producto nuevoProducto = productoMapper.toEntity(requestDTO);

        nuevoProducto.setCategoria(categoriaEncontrada);
        nuevoProducto.setTienda(tiendaRef);

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
        productoRepository.findFirstByCodigoOrderByActivoDesc(requestDTO.getCodigo())
        .ifPresent(p -> {
            if (Boolean.TRUE.equals(p.getActivo()) && !p.getId().equals(id.toString()))
                throw new IllegalArgumentException("El código de barras '" + requestDTO.getCodigo() + "' ya pertenece a otro producto.");
        });

        Producto productoEncontrado = productoRepository.findById(id.toString()).orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        Categoria categoriaEncontrada = categoriaRepository.findById(requestDTO.getCategoriaId().toString()).orElseThrow(() -> new RecursoNoEncontradoException("Categoria no encontrada"));
        productoEncontrado.setNombre(requestDTO.getNombre());
        productoEncontrado.setCodigo(requestDTO.getCodigo());
        productoEncontrado.setPrecio(requestDTO.getPrecio());
        productoEncontrado.setStock(requestDTO.getStock());
        productoEncontrado.setCategoria(categoriaEncontrada);

        return productoMapper.toDto(productoRepository.save(productoEncontrado));

    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarProductosMasVendidos()
    {
        //devolveremos los 15 productos más vendidos
        Pageable top15 = PageRequest.of(0, 15);

        List<Producto> productos = productoRepository.findTopProductosMasVendidos(top15);

        return productoMapper.toDtoList(productos);

    }

    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> buscarProductoPorNombre(String termino, int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size);

        Page<Producto> coincidencias = productoRepository.buscarActivosPorNombreOCodigo(termino, pageable);

        return coincidencias.map(productoMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> filtrarCategoria(Collection<String> categorias, int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size);

        Page<Producto> coincidencias;

        // Si la lista viene nula o vacia (el usuario no ha seleccionado ningún filtro)
        if (categorias == null || categorias.isEmpty())
            coincidencias = productoRepository.findAllByActivo(true, pageable); // devolvemos todos los productos
        else
            //De lo contrario hacemos el filtro de esas categorias
            coincidencias = productoRepository.findByCategoria_IdInAndActivo(categorias, true, pageable);


        return coincidencias.map(productoMapper::toDto);
    }


}

