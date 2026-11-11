package com.uniremington.armamento.model.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExistenciaVO(
        Long idExistencia, Integer idProducto, Integer idAlmacen, String lote,
        LocalDate fechaVencimiento, Integer cantidad, LocalDateTime actualizadoEn) {
    public ExistenciaVO() {
        this(null, null, null, null, null, null, null);
    }
}