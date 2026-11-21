package com.uniremington.armamento.service.impl;

import com.uniremington.armamento.dto.ProductoDto;
import com.uniremington.armamento.entity.Categoria;
import com.uniremington.armamento.entity.Marca;
import com.uniremington.armamento.entity.Producto;
import com.uniremington.armamento.exception.RecursoNoEncontradoException;
import com.uniremington.armamento.mapper.ProductoMapper;
import com.uniremington.armamento.repository.CategoriaRepository;
import com.uniremington.armamento.repository.MarcaRepository;
import com.uniremington.armamento.repository.ProductoRepository;
import com.uniremington.armamento.service.AuditoriaService;
import com.uniremington.armamento.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final ProductoMapper productoMapper;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoDto> buscar(String consulta, Pageable pageable) {
        String termino = consulta == null ? "" : consulta.trim();
        Page<Producto> productos = termino.isEmpty()
                ? productoRepository.findAll(pageable)
                : productoRepository.findByNombreContainingIgnoreCaseOrSkuContainingIgnoreCase(
                        termino, termino, pageable);
        return productos.map(productoMapper::aDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDto obtener(Integer id) {
        return productoMapper.aDto(productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id)));
    }

    @Override
    @Transactional
    public ProductoDto crear(ProductoDto dto) {
        validarSkuDisponible(dto.getSku(), null);
        Producto producto = productoMapper.aEntidad(dto);
        producto.setSku(dto.getSku().trim());
        producto.setNombre(dto.getNombre().trim());
        producto.setUnidadMedida(dto.getUnidadMedida().trim());
        asignarRelaciones(producto, dto);
        try {
            Producto guardado = productoRepository.save(producto);
            ProductoDto resultado = productoMapper.aDto(guardado);
            auditoriaService.registrar("CREAR", "producto", guardado.getId(), null, resultado);
            return resultado;
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException("El SKU ya está registrado.", exception);
        }
    }

    @Override
    @Transactional
    public ProductoDto actualizar(Integer id, ProductoDto dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));
        ProductoDto antes = productoMapper.aDto(producto);
        validarSkuDisponible(dto.getSku(), id);
        producto.setSku(dto.getSku().trim());
        producto.setNombre(dto.getNombre().trim());
        producto.setDescripcion(dto.getDescripcion());
        producto.setModelo(dto.getModelo());
        producto.setCalibre(dto.getCalibre());
        producto.setUnidadMedida(dto.getUnidadMedida().trim());
        producto.setControlaSerial(dto.isControlaSerial());
        producto.setControlaLote(dto.isControlaLote());
        producto.setPrecioCompraRef(dto.getPrecioCompraRef());
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setPorcentajeIva(dto.getPorcentajeIva());
        producto.setStockMinimo(dto.getStockMinimo());
        producto.setActivo(dto.isActivo());
        asignarRelaciones(producto, dto);
        ProductoDto resultado = productoMapper.aDto(productoRepository.save(producto));
        auditoriaService.registrar("ACTUALIZAR", "producto", id, antes, resultado);
        return resultado;
    }

    @Override
    @Transactional
    public void desactivar(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));
        ProductoDto antes = productoMapper.aDto(producto);
        producto.setActivo(false);
        ProductoDto despues = productoMapper.aDto(productoRepository.save(producto));
        auditoriaService.registrar("DESACTIVAR", "producto", id, antes, despues);
    }

    private void asignarRelaciones(Producto producto, ProductoDto dto) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría", dto.getCategoriaId()));
        producto.setCategoria(categoria);
        if (dto.getMarcaId() == null) {
            producto.setMarca(null);
        } else {
            Marca marca = marcaRepository.findById(dto.getMarcaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Marca", dto.getMarcaId()));
            producto.setMarca(marca);
        }
    }

    private void validarSkuDisponible(String sku, Integer idActual) {
        if (productoRepository.existsBySkuIgnoreCase(sku.trim())
                && (idActual == null || !productoRepository.findById(idActual)
                        .map(producto -> producto.getSku().equalsIgnoreCase(sku.trim()))
                        .orElse(false))) {
            throw new IllegalArgumentException("El SKU ya está registrado.");
        }
    }
}
