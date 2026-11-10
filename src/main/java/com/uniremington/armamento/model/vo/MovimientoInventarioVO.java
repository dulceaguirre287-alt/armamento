package com.uniremington.armamento.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoInventarioVO(
        Long idMovimiento, Integer idProducto, Integer idAlmacen, Long idUnidad,
        String lote, String tipoMovimiento, Integer cantidad, Integer saldoPosterior,
        BigDecimal costoUnitario, String referenciaTipo, Long referenciaId,
        Integer idUsuario, LocalDateTime fecha, String observaciones) {
    public MovimientoInventarioVO() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }
}