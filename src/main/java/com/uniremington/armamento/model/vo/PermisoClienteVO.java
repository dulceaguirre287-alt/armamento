package com.uniremington.armamento.vo;

import java.time.LocalDate;

public record PermisoClienteVO(
        Integer idPermiso, Integer idCliente, String tipoPermiso, String numeroPermiso,
        String autoridadEmisora, LocalDate fechaExpedicion, LocalDate fechaVencimiento,
        String estado) {
    public PermisoClienteVO() {
        this(null, null, null, null, null, null, null, null);
    }
}