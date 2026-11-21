package com.uniremington.armamento.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductoDto {
    private Integer id;

    @NotBlank(message = "El SKU es obligatorio.")
    @Size(max = 80, message = "El SKU no puede superar 80 caracteres.")
    private String sku;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 180, message = "El nombre no puede superar 180 caracteres.")
    private String nombre;

    @Size(max = 1000, message = "La descripción no puede superar 1000 caracteres.")
    private String descripcion;

    @NotNull(message = "Selecciona una categoría.")
    private Integer categoriaId;

    private String categoriaNombre;
    private Integer marcaId;
    private String marcaNombre;

    @Size(max = 100, message = "El modelo no puede superar 100 caracteres.")
    private String modelo;

    @Size(max = 50, message = "El calibre no puede superar 50 caracteres.")
    private String calibre;

    @NotBlank(message = "La unidad de medida es obligatoria.")
    @Size(max = 30, message = "La unidad de medida no puede superar 30 caracteres.")
    private String unidadMedida = "unidad";

    private boolean controlaSerial;
    private boolean controlaLote;

    @NotNull(message = "El precio de compra es obligatorio.")
    @PositiveOrZero(message = "El precio de compra no puede ser negativo.")
    @Digits(integer = 10, fraction = 2, message = "El precio de compra admite hasta 10 enteros y 2 decimales.")
    private BigDecimal precioCompraRef = BigDecimal.ZERO;

    @NotNull(message = "El precio de venta es obligatorio.")
    @PositiveOrZero(message = "El precio de venta no puede ser negativo.")
    @Digits(integer = 10, fraction = 2, message = "El precio de venta admite hasta 10 enteros y 2 decimales.")
    private BigDecimal precioVenta = BigDecimal.ZERO;

    @NotNull(message = "El porcentaje de IVA es obligatorio.")
    @DecimalMin(value = "0.00", message = "El IVA no puede ser negativo.")
    @DecimalMax(value = "100.00", message = "El IVA no puede superar 100%.")
    @Digits(integer = 3, fraction = 2, message = "El IVA admite hasta 3 enteros y 2 decimales.")
    private BigDecimal porcentajeIva = BigDecimal.ZERO;

    @PositiveOrZero(message = "El stock mínimo no puede ser negativo.")
    private int stockMinimo;

    private boolean activo = true;
}
