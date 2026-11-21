package com.uniremington.armamento.service;

import com.uniremington.armamento.dto.UsuarioFormularioDto;
import com.uniremington.armamento.dto.UsuarioVistaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioService {
    Page<UsuarioVistaDto> buscar(String consulta, Pageable pageable);

    UsuarioVistaDto obtener(Integer id);

    UsuarioVistaDto crear(UsuarioFormularioDto dto);

    UsuarioVistaDto actualizar(Integer id, UsuarioFormularioDto dto);

    void desactivar(Integer id);
}
