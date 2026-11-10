package com.uniremington.armamento.vo;

import java.time.LocalDateTime;

public record AuditoriaVO(
        Long idAuditoria, Integer idUsuario, String accion, String tabla,
        String registroId, String datosAntes, String datosDespues, String ipOrigen,
        LocalDateTime fecha) {
    public AuditoriaVO() {
        this(null, null, null, null, null, null, null, null, null);
    }
}