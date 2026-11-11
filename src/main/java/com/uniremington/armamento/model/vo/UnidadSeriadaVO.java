package com.uniremington.armamento.model.vo;

import java.time.LocalDateTime;

public record UnidadSeriadaVO(
        Long idUnidad, Integer idProducto, String numeroSerie, Integer idAlmacen,
        Long idDetalleCompra, Long idDetalleVenta, String estado,
        LocalDateTime fechaIngreso, LocalDateTime fechaSalida, String observaciones) {
    public UnidadSeriadaVO() {
        this(null, null, null, null, null, null, null, null, null, null);
    }
}