package com.uniremington.armamento.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioVistaDto {
    private Integer id;
    private String nombres;
    private String apellidos;
    private String email;
    private String username;
    private Integer rolId;
    private String rolNombre;
    private boolean activo;
}
