package com.uniremington.armamento.model.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProveedorVO(
        Integer idProveedor, String razonSocial, String nit, String numeroLicencia,
        LocalDate licenciaVence, String contactoNombre, String email, String telefono,
        String direccion, String ciudad, String pais, String condicionesPago,
        Boolean activo, LocalDateTime creadoEn, LocalDateTime actualizadoEn) {
    public ProveedorVO() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }
}