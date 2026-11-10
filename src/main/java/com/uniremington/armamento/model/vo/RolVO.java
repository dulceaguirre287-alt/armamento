package com.uniremington.armamento.vo;

public record RolVO(Integer idRol, String nombre, String descripcion) {
    public RolVO() {
        this(null, null, null);
    }
}