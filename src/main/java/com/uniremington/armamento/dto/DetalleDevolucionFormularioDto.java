package com.uniremington.armamento.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DetalleDevolucionFormularioDto {
    @NotNull(message = "Selecciona un producto.")
    private Integer productoId;

    private Long unidadId;

    @Positive(message = "La cantidad debe ser mayor que cero.")
    private int cantidad;

    @Size(max = 80, message = "El lote no puede superar 80 caracteres.")
    private String lote;

    @NotNull(message = "Indica la condición del producto.")
    @Size(max = 60, message = "La condición no puede superar 60 caracteres.")
    private String condicion;

    private boolean reingresaStock;
}
