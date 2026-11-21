package com.uniremington.armamento.exception;

public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String recurso, Object id) {
        super(recurso + " con identificador " + id + " no existe.");
    }
}
