package com.uniremington.armamento.model.vo;

import java.math.BigDecimal;

public record DetalleVentaVO(
        Long idDetalleVenta, Long idVenta, Integer idProducto, Integer cantidad,
        BigDecimal precioUnitario, BigDecimal descuento, BigDecimal porcentajeIva,
        String lote, BigDecimal subtotal) {
    public DetalleVentaVO() {
        this(null, null, null, null, null, null, null, null, null);
    }
}