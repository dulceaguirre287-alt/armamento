package com.uniremington.armamento.vo;

import java.time.LocalDateTime;

public record ClienteVO(
        Integer idCliente, String tipoCliente, String tipoDocumento, String numeroDocumento,
        String nombreRazon, String email, String telefono, String direccion, String ciudad,
        Boolean activo, LocalDateTime creadoEn) {
    public ClienteVO() {
        this(null, null, null, null, null, null, null, null, null, null, null);
    }
}