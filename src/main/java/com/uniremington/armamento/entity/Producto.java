package com.uniremington.armamento.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "producto", uniqueConstraints = @UniqueConstraint(name = "uk_producto_sku", columnNames = "sku"))
@Getter
@Setter
@NoArgsConstructor
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer id;

    @Column(nullable = false, length = 80)
    private String sku;

    @Column(nullable = false, length = 180)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_marca")
    private Marca marca;

    @Column(length = 100)
    private String modelo;

    @Column(length = 50)
    private String calibre;

    @Column(name = "unidad_medida", nullable = false, length = 30)
    private String unidadMedida = "unidad";

    @Column(name = "controla_serial", nullable = false)
    private boolean controlaSerial;

    @Column(name = "controla_lote", nullable = false)
    private boolean controlaLote;

    @Column(name = "precio_compra_ref", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioCompraRef = BigDecimal.ZERO;

    @Column(name = "precio_venta", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioVenta = BigDecimal.ZERO;

    @Column(name = "porcentaje_iva", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeIva = BigDecimal.ZERO;

    @Column(name = "stock_minimo", nullable = false)
    private int stockMinimo;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    @PrePersist
    void alCrear() {
        LocalDateTime ahora = LocalDateTime.now();
        creadoEn = ahora;
        actualizadoEn = ahora;
    }

    @PreUpdate
    void alActualizar() {
        actualizadoEn = LocalDateTime.now();
    }
}
