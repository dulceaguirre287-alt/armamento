package com.uniremington.armamento.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "devolucion", uniqueConstraints = @UniqueConstraint(
        name = "uk_devolucion_numero", columnNames = "numero_devolucion"))
@Getter
@Setter
@NoArgsConstructor
public class Devolucion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_devolucion")
    private Long id;

    @Column(name = "numero_devolucion", nullable = false, length = 60)
    private String numeroDevolucion;

    @Column(nullable = false, length = 30)
    private String tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta")
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra")
    private Compra compra;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_almacen", nullable = false)
    private Almacen almacen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_devolucion", nullable = false)
    private LocalDateTime fechaDevolucion;

    @Column(nullable = false, length = 250)
    private String motivo;

    @Column(length = 1000)
    private String descripcion;

    @Column(name = "total_reembolso", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalReembolso = BigDecimal.ZERO;

    @Column(nullable = false, length = 30)
    private String estado = "PENDIENTE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprobada_por")
    private Usuario aprobadaPor;

    @OneToMany(mappedBy = "devolucion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleDevolucion> detalles = new ArrayList<>();

    @PrePersist
    void alCrear() {
        if (fechaDevolucion == null) {
            fechaDevolucion = LocalDateTime.now();
        }
    }
}
