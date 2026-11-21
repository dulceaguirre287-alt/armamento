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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "auditoria")
@Getter
@Setter
@NoArgsConstructor
public class Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(nullable = false, length = 40)
    private String accion;

    @Column(nullable = false, length = 80)
    private String tabla;

    @Column(name = "registro_id", length = 80)
    private String registroId;

    @Column(name = "datos_antes", columnDefinition = "LONGTEXT")
    private String datosAntes;

    @Column(name = "datos_despues", columnDefinition = "LONGTEXT")
    private String datosDespues;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    void alCrear() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}
