package com.uniremington.armamento.model.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VentaVO(
        Long idVenta, String numeroVenta, Integer idCliente, Integer idUsuario,
        Integer idAlmacen, Integer idPermiso, LocalDateTime fechaVenta,
        BigDecimal subtotal, BigDecimal descuento, BigDecimal iva, BigDecimal total,
        String metodoPago, String estado, Integer autorizadaPor,
        LocalDateTime fechaAutorizacion, String observaciones) {
    public VentaVO() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }
}