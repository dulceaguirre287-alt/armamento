package com.uniremington.armamento.service;

import com.uniremington.armamento.dto.ProductoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductoService {
    Page<ProductoDto> buscar(String consulta, Pageable pageable);

    ProductoDto obtener(Integer id);

    ProductoDto crear(ProductoDto dto);

    ProductoDto actualizar(Integer id, ProductoDto dto);

    void desactivar(Integer id);
}
