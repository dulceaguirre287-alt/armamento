package com.uniremington.armamento.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categoria", uniqueConstraints = @UniqueConstraint(name = "uk_categoria_nombre", columnNames = "nombre"))
@Getter
@Setter
@NoArgsConstructor
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(name = "tipo_control", length = 50)
    private String tipoControl;

    @Column(name = "requiere_permiso", nullable = false)
    private boolean requierePermiso;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private boolean activo = true;
}
