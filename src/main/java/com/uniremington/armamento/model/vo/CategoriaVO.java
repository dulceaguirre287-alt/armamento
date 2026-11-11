package com.uniremington.armamento.model.vo;

public record CategoriaVO(
        Integer idCategoria, String nombre, String tipoControl, Boolean requierePermiso,
        String descripcion, Boolean activo) {
    public CategoriaVO() {
        this(null, null, null, null, null, null);
    }
}