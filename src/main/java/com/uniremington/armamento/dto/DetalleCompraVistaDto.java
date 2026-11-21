package com.uniremington.armamento.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DetalleCompraVistaDto {
    private Integer productoId;
    private String productoSku;
    private String productoNombre;
    private int cantidadPedida;
    private int cantidadRecibida;
    private BigDecimal precioUnitario;
    private BigDecimal porcentajeIva;
    private String lote;
    private BigDecimal subtotal;
}
