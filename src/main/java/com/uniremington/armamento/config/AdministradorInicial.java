package com.uniremington.armamento.config;

import com.uniremington.armamento.entity.Rol;
import com.uniremington.armamento.entity.Usuario;
import com.uniremington.armamento.repository.RolRepository;
import com.uniremington.armamento.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdministradorInicial implements ApplicationRunner {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.username:}")
    private String username;

    @Value("${app.bootstrap.password:}")
    private String password;

    @Override
    @Transactional
    public void run(ApplicationArguments arguments) {
        if (username.isBlank() && password.isBlank()) {
            return;
        }
        if (username.isBlank() || password.length() < 12) {
            throw new IllegalStateException(
                    "Configure APP_ADMIN_USERNAME y APP_ADMIN_PASSWORD (mínimo 12 caracteres) para crear el administrador inicial.");
        }
        if (usuarioRepository.existsByUsername(username)) {
            return;
        }

        Rol rol = rolRepository.findByNombre("ADMINISTRADOR")
                .orElseThrow(() -> new IllegalStateException("No existe el rol inicial ADMINISTRADOR."));
        Usuario admin = new Usuario();
        admin.setRol(rol);
        admin.setNombres("Administrador");
        admin.setApellidos("Inicial");
        admin.setEmail(username + "@localhost");
        admin.setUsername(username);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setActivo(true);
        usuarioRepository.save(admin);
    }
}
