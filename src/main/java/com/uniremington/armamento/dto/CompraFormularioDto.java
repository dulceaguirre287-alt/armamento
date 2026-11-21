package com.uniremington.armamento.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompraFormularioDto {
    @NotNull(message = "Selecciona un proveedor.")
    private Integer proveedorId;

    @NotNull(message = "Selecciona un almacén.")
    private Integer almacenId;

    @Size(max = 80, message = "La factura no puede superar 80 caracteres.")
    private String numeroFacturaProveedor;

    @Size(max = 80, message = "La guía no puede superar 80 caracteres.")
    private String guiaTransporte;

    @Size(max = 1000, message = "Las observaciones no pueden superar 1000 caracteres.")
    private String observaciones;

    @Valid
    @NotEmpty(message = "Agrega al menos un producto a la compra.")
    private List<DetalleCompraFormularioDto> detalles = new ArrayList<>();
}
