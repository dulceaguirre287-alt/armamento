package com.uniremington.armamento.vo;

import java.math.BigDecimal;

public record DetalleDevolucionVO(
        Long idDetalleDevolucion, Long idDevolucion, Integer idProducto, Long idUnidad,
        String lote, Integer cantidad, BigDecimal precioUnitario, String condicion,
        Boolean reingresaStock) {
    public DetalleDevolucionVO() {
        this(null, null, null, null, null, null, null, null, null);
    }
}