package com.uniremington.armamento.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DetalleCompraVO(
        Long idDetalleCompra, Long idCompra, Integer idProducto, Integer cantidadPedida,
        Integer cantidadRecibida, BigDecimal precioUnitario, BigDecimal porcentajeIva,
        String lote, LocalDate fechaVencimiento, BigDecimal subtotal) {
    public DetalleCompraVO() {
        this(null, null, null, null, null, null, null, null, null, null);
    }
}