package com.uniremington.armamento.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DevolucionVO(
        Long idDevolucion, String numeroDevolucion, String tipo, Long idVenta,
        Long idCompra, Integer idAlmacen, Integer idUsuario, LocalDateTime fechaDevolucion,
        String motivo, String descripcion, BigDecimal totalReembolso, String estado,
        Integer aprobadaPor) {
    public DevolucionVO() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null);
    }
}