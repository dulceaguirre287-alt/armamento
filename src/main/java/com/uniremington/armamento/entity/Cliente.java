package com.uniremington.armamento.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cliente", uniqueConstraints = @UniqueConstraint(
        name = "uk_cliente_documento", columnNames = {"tipo_documento", "numero_documento"}))
@Getter
@Setter
@NoArgsConstructor
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer id;

    @Column(name = "tipo_cliente", nullable = false, length = 40)
    private String tipoCliente;

    @Column(name = "tipo_documento", nullable = false, length = 30)
    private String tipoDocumento;

    @Column(name = "numero_documento", nullable = false, length = 50)
    private String numeroDocumento;

    @Column(name = "nombre_razon", nullable = false, length = 180)
    private String nombreRazon;

    @Column(length = 190)
    private String email;

    @Column(length = 30)
    private String telefono;

    @Column(length = 250)
    private String direccion;

    @Column(length = 100)
    private String ciudad;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    void alCrear() {
        creadoEn = LocalDateTime.now();
    }
}
