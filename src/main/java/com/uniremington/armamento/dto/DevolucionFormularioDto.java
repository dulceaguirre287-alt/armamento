package com.uniremington.armamento.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DevolucionFormularioDto {
    @NotBlank(message = "Selecciona el tipo de devolución.")
    private String tipo;

    private Long ventaId;
    private Long compraId;

    @NotBlank(message = "El motivo es obligatorio.")
    @Size(max = 250, message = "El motivo no puede superar 250 caracteres.")
    private String motivo;

    @Size(max = 1000, message = "La descripción no puede superar 1000 caracteres.")
    private String descripcion;

    @Valid
    @NotEmpty(message = "Agrega al menos un producto a la devolución.")
    private List<DetalleDevolucionFormularioDto> detalles = new ArrayList<>();
}
