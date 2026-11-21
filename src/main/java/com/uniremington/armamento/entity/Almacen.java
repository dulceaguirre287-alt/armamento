package com.uniremington.armamento.entity;

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
@Table(name = "almacen", uniqueConstraints = @UniqueConstraint(name = "uk_almacen_nombre", columnNames = "nombre"))
@Getter
@Setter
@NoArgsConstructor
public class Almacen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_almacen")
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 250)
    private String direccion;

    @Column(length = 100)
    private String ciudad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable")
    private Usuario responsable;

    @Column(nullable = false)
    private boolean activo = true;
}
