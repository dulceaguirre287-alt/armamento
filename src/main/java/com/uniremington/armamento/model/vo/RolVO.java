package com.uniremington.armamento.model.vo;

public record RolVO(Integer idRol, String nombre, String descripcion) {
    public RolVO() {
        this(null, null, null);
    }
}