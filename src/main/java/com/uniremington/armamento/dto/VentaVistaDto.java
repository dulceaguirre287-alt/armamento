package com.uniremington.armamento.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VentaVistaDto {
    private Long id;
    private String numeroVenta;
    private Integer clienteId;
    private String clienteNombre;
    private String usuarioNombre;
    private Integer almacenId;
    private String almacenNombre;
    private LocalDateTime fechaVenta;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal iva;
    private BigDecimal total;
    private String metodoPago;
    private String estado;
    private String observaciones;
    private List<DetalleVentaVistaDto> detalles;
}
