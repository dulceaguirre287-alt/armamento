package com.uniremington.armamento.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VentaFormularioDto {
    @NotNull(message = "Selecciona un cliente.")
    private Integer clienteId;

    @NotNull(message = "Selecciona un almacén.")
    private Integer almacenId;

    @NotBlank(message = "Selecciona un método de pago.")
    private String metodoPago;

    @Size(max = 1000, message = "Las observaciones no pueden superar 1000 caracteres.")
    private String observaciones;

    @Valid
    @NotEmpty(message = "Agrega al menos un producto a la venta.")
    private List<DetalleVentaFormularioDto> detalles = new ArrayList<>();
}
