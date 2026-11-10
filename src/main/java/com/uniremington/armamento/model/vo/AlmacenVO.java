package com.uniremington.armamento.vo;

public record AlmacenVO(
        Integer idAlmacen, String nombre, String direccion, String ciudad,
        Integer responsable, Boolean activo) {
    public AlmacenVO() {
        this(null, null, null, null, null, null);
    }
}