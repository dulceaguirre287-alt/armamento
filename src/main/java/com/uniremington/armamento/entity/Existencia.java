package com.uniremington.armamento.entity;

import java.time.LocalDate;
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
@Table(name = "existencia", uniqueConstraints = @UniqueConstraint(
        name = "uk_existencia_producto_almacen_lote", columnNames = {"id_producto", "id_almacen", "lote"}))
@Getter
@Setter
@NoArgsConstructor
public class Existencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_existencia")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_almacen", nullable = false)
    private Almacen almacen;

    @Column(nullable = false, length = 80)
    private String lote = "";

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(nullable = false)
    private int cantidad;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    @PrePersist
    @PreUpdate
    void alGuardar() {
        actualizadoEn = LocalDateTime.now();
    }
}
