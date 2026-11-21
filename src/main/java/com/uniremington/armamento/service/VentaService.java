package com.uniremington.armamento.service;

import com.uniremington.armamento.dto.VentaFormularioDto;
import com.uniremington.armamento.dto.VentaVistaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VentaService {
    Page<VentaVistaDto> buscar(String consulta, Pageable pageable);

    VentaVistaDto obtener(Long id);

    VentaVistaDto crear(VentaFormularioDto dto);

    void anular(Long id);
}
