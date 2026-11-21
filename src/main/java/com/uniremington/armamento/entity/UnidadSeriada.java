package com.uniremington.armamento.entity;

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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "unidad_seriada", uniqueConstraints = @UniqueConstraint(
        name = "uk_unidad_seriada_numero", columnNames = "numero_serie"))
@Getter
@Setter
@NoArgsConstructor
public class UnidadSeriada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidad")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(name = "numero_serie", nullable = false, length = 100)
    private String numeroSerie;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_almacen", nullable = false)
    private Almacen almacen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_detalle_compra")
    private com.uniremington.armamento.entity.DetalleCompra detalleCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_detalle_venta")
    private DetalleVenta detalleVenta;

    @Column(nullable = false, length = 30)
    private String estado = "DISPONIBLE";

    @Column(name = "fecha_ingreso")
    private LocalDateTime fechaIngreso;

    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    @Column(length = 500)
    private String observaciones;

    @PrePersist
    void alCrear() {
        if (fechaIngreso == null) {
            fechaIngreso = LocalDateTime.now();
        }
    }
}
