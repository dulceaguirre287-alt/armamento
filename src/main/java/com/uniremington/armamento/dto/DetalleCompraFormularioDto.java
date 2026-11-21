package com.uniremington.armamento.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DetalleCompraFormularioDto {
    @NotNull(message = "Selecciona un producto.")
    private Integer productoId;

    @Positive(message = "La cantidad debe ser mayor que cero.")
    private int cantidad;

    @NotNull(message = "El precio unitario es obligatorio.")
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor que cero.")
    private BigDecimal precioUnitario;

    @Size(max = 80, message = "El lote no puede superar 80 caracteres.")
    private String lote;

    private LocalDate fechaVencimiento;

    private List<@Size(max = 100, message = "El número de serie no puede superar 100 caracteres.") String>
            numerosSerie = new ArrayList<>();
}
