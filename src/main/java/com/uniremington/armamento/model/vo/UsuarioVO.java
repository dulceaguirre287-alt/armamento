package com.uniremington.armamento.vo;

import java.time.LocalDateTime;

public record UsuarioVO(
        Integer idUsuario, Integer idRol, String tipoDocumento, String numeroDocumento,
        String nombres, String apellidos, String email, String telefono, String username,
        String passwordHash, Integer intentosFallidos, LocalDateTime bloqueadoHasta,
        LocalDateTime ultimoAcceso, Boolean activo, LocalDateTime creadoEn,
        LocalDateTime actualizadoEn) {
    public UsuarioVO() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }
}