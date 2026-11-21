package com.uniremington.armamento.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DetalleDevolucionVistaDto {
    private Integer productoId;
    private String productoSku;
    private String productoNombre;
    private Long unidadId;
    private String numeroSerie;
    private String lote;
    private int cantidad;
    private BigDecimal precioUnitario;
    private String condicion;
    private boolean reingresaStock;
}
