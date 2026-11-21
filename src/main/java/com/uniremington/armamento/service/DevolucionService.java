package com.uniremington.armamento.service;

import com.uniremington.armamento.dto.DevolucionFormularioDto;
import com.uniremington.armamento.dto.DevolucionVistaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DevolucionService {
    Page<DevolucionVistaDto> buscar(String consulta, Pageable pageable);

    DevolucionVistaDto obtener(Long id);

    DevolucionVistaDto crear(DevolucionFormularioDto dto);

    void aprobar(Long id);

    void rechazar(Long id);
}
