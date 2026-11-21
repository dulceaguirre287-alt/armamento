package com.uniremington.armamento.service;

import com.uniremington.armamento.dto.CompraFormularioDto;
import com.uniremington.armamento.dto.CompraVistaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompraService {
    Page<CompraVistaDto> buscar(String consulta, Pageable pageable);

    CompraVistaDto obtener(Long id);

    CompraVistaDto crear(CompraFormularioDto dto);

    void anular(Long id);
}
