package com.uniremington.armamento.dto;

import java.time.LocalDateTime;

public record MovimientoVistaDto(
        String producto,
        String almacen,
        String tipo,
        int cantidad,
        int saldoPosterior,
        LocalDateTime fecha) {
}
