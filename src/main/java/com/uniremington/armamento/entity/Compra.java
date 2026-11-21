package com.uniremington.armamento.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "compra", uniqueConstraints = @UniqueConstraint(name = "uk_compra_numero", columnNames = "numero_compra"))
@Getter
@Setter
@NoArgsConstructor
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Long id;

    @Column(name = "numero_compra", nullable = false, length = 60)
    private String numeroCompra;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_almacen", nullable = false)
    private Almacen almacen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDate fechaCompra;

    @Column(name = "fecha_recepcion")
    private LocalDateTime fechaRecepcion;

    @Column(name = "numero_factura_proveedor", length = 80)
    private String numeroFacturaProveedor;

    @Column(name = "guia_transporte", length = 80)
    private String guiaTransporte;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal descuento;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal iva;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, length = 30)
    private String estado;

    @Column(length = 1000)
    private String observaciones;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    @OneToMany(mappedBy = "compra", fetch = FetchType.LAZY)
    private List<DetalleCompra> detalles = new ArrayList<>();

    @PrePersist
    void alCrear() {
        if (fechaCompra == null) {
            fechaCompra = LocalDate.now();
        }
        creadoEn = LocalDateTime.now();
    }
}
