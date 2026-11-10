package com.uniremington.armamento.vo;

public record MarcaVO(Integer idMarca, String nombre, String paisOrigen, Boolean activo) {
    public MarcaVO() {
        this(null, null, null, null);
    }
}