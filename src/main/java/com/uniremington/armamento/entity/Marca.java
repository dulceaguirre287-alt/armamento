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
@Table(name = "marca", uniqueConstraints = @UniqueConstraint(name = "uk_marca_nombre", columnNames = "nombre"))
@Getter
@Setter
@NoArgsConstructor
public class Marca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_marca")
    private Integer id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(name = "pais_origen", length = 100)
    private String paisOrigen;

    @Column(nullable = false)
    private boolean activo = true;
}
