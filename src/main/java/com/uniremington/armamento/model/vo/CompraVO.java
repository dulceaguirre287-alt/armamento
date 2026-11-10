package com.uniremington.armamento.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CompraVO(
        Long idCompra, String numeroCompra, Integer idProveedor, Integer idAlmacen,
        Integer idUsuario, LocalDate fechaCompra, LocalDateTime fechaRecepcion,
        String numeroFacturaProveedor, String guiaTransporte, BigDecimal subtotal,
        BigDecimal descuento, BigDecimal iva, BigDecimal total, String estado,
        String observaciones, LocalDateTime creadoEn) {
    public CompraVO() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }
}