package com.uniremington.armamento.security;

import java.time.LocalDateTime;

import com.uniremington.armamento.entity.Usuario;
import com.uniremington.armamento.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioAuthenticationService implements UserDetailsService {
    private static final int MAX_INTENTOS = 5;
    private static final int MINUTOS_BLOQUEO = 15;

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas"));
        boolean bloqueado = usuario.getBloqueadoHasta() != null
                && usuario.getBloqueadoHasta().isAfter(LocalDateTime.now());
        return User.withUsername(usuario.getUsername())
                .password(usuario.getPasswordHash())
                .authorities("ROLE_" + usuario.getRol().getNombre())
                .disabled(!usuario.isActivo())
                .accountLocked(bloqueado)
                .build();
    }

    @Transactional
    public void registrarFallo(String username) {
        if (username == null || username.isBlank()) {
            return;
        }
        usuarioRepository.findByUsername(username).ifPresent(usuario -> {
            int intentos = usuario.getIntentosFallidos() + 1;
            usuario.setIntentosFallidos(intentos);
            if (intentos >= MAX_INTENTOS) {
                usuario.setBloqueadoHasta(LocalDateTime.now().plusMinutes(MINUTOS_BLOQUEO));
                usuario.setIntentosFallidos(0);
            }
        });
    }

    @Transactional
    public void registrarAcceso(String username) {
        usuarioRepository.findByUsername(username).ifPresent(usuario -> {
            usuario.setIntentosFallidos(0);
            usuario.setBloqueadoHasta(null);
            usuario.setUltimoAcceso(LocalDateTime.now());
        });
    }
}
