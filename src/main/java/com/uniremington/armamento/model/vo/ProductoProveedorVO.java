package com.uniremington.armamento.model.vo;

import java.math.BigDecimal;

public record ProductoProveedorVO(
        Integer idProducto, Integer idProveedor, String codigoProveedor,
        BigDecimal ultimoPrecio, Short diasEntrega) {
    public ProductoProveedorVO() {
        this(null, null, null, null, null);
    }
}