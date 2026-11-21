package com.uniremington.armamento.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "permiso_cliente", uniqueConstraints = @UniqueConstraint(
        name = "uk_permiso_numero", columnNames = "numero_permiso"))
@Getter
@Setter
@NoArgsConstructor
public class PermisoCliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permiso")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @Column(name = "tipo_permiso", nullable = false, length = 60)
    private String tipoPermiso;

    @Column(name = "numero_permiso", nullable = false, length = 80)
    private String numeroPermiso;

    @Column(name = "autoridad_emisora", length = 150)
    private String autoridadEmisora;

    @Column(name = "fecha_expedicion", nullable = false)
    private LocalDate fechaExpedicion;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(nullable = false, length = 30)
    private String estado = "VIGENTE";
}
