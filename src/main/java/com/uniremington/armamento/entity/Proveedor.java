package com.uniremington.armamento.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proveedor", uniqueConstraints = @UniqueConstraint(name = "uk_proveedor_nit", columnNames = "nit"))
@Getter
@Setter
@NoArgsConstructor
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer id;

    @Column(name = "razon_social", nullable = false, length = 180)
    private String razonSocial;

    @Column(nullable = false, length = 30)
    private String nit;

    @Column(name = "numero_licencia", length = 80)
    private String numeroLicencia;

    @Column(name = "licencia_vence")
    private LocalDate licenciaVence;

    @Column(name = "contacto_nombre", length = 120)
    private String contactoNombre;

    @Column(length = 190)
    private String email;

    @Column(length = 30)
    private String telefono;

    @Column(length = 250)
    private String direccion;

    @Column(length = 100)
    private String ciudad;

    @Column(length = 100)
    private String pais;

    @Column(name = "condiciones_pago", length = 150)
    private String condicionesPago;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "creado_en", nullable = false)
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
