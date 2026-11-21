package com.uniremington.armamento.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioFormularioDto {
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 120, message = "El nombre no puede superar 120 caracteres.")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios.")
    @Size(max = 120, message = "Los apellidos no pueden superar 120 caracteres.")
    private String apellidos;

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "Ingresa un correo válido.")
    @Size(max = 190, message = "El correo no puede superar 190 caracteres.")
    private String email;

    @NotBlank(message = "El nombre de usuario es obligatorio.")
    @Size(max = 80, message = "El nombre de usuario no puede superar 80 caracteres.")
    private String username;

    @NotNull(message = "Selecciona un rol.")
    private Integer rolId;

    @Size(max = 100, message = "La contraseña no puede superar 100 caracteres.")
    private String password;

    private boolean activo = true;
}
