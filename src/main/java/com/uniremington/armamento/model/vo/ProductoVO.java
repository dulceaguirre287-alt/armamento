package com.uniremington.armamento.model.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductoVO(
        Integer idProducto, String sku, String nombre, String descripcion,
        Integer idCategoria, Integer idMarca, String modelo, String calibre,
        String unidadMedida, Boolean controlaSerial, Boolean controlaLote,
        BigDecimal precioCompraRef, BigDecimal precioVenta, BigDecimal porcentajeIva,
        Integer stockMinimo, Boolean activo, LocalDateTime creadoEn,
        LocalDateTime actualizadoEn) {
    public ProductoVO() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }
}