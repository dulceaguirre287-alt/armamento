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
public class DevolucionVistaDto {
    private Long id;
    private String numeroDevolucion;
    private String tipo;
    private Long ventaId;
    private String ventaNumero;
    private Long compraId;
    private String compraNumero;
    private String almacenNombre;
    private String usuarioNombre;
    private LocalDateTime fechaDevolucion;
    private String motivo;
    private String descripcion;
    private BigDecimal totalReembolso;
    private String estado;
    private List<DetalleDevolucionVistaDto> detalles;
}
