package com.uniremington.armamento.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompraVistaDto {
    private Long id;
    private String numeroCompra;
    private String proveedorNombre;
    private String almacenNombre;
    private String usuarioNombre;
    private LocalDate fechaCompra;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal iva;
    private BigDecimal total;
    private String estado;
    private List<DetalleCompraVistaDto> detalles;
}
