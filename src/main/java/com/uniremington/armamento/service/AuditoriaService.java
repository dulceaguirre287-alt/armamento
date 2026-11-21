package com.uniremington.armamento.service;

import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniremington.armamento.entity.Auditoria;
import com.uniremington.armamento.entity.Usuario;
import com.uniremington.armamento.repository.AuditoriaRepository;
import com.uniremington.armamento.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class AuditoriaService {
    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

    public void registrar(String accion, String tabla, Object registroId, Object antes, Object despues) {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario(usuarioActual());
        auditoria.setAccion(accion);
        auditoria.setTabla(tabla);
        auditoria.setRegistroId(registroId == null ? null : registroId.toString());
        auditoria.setDatosAntes(serializar(antes));
        auditoria.setDatosDespues(serializar(despues));
        auditoria.setIpOrigen(ipActual());
        auditoria.setFecha(LocalDateTime.now());
        auditoriaRepository.save(auditoria);
    }

    private Usuario usuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        return usuarioRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("El usuario autenticado no existe."));
    }

    private String serializar(Object valor) {
        if (valor == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(valor);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("No fue posible guardar los datos de auditoría.", exception);
        }
    }

    private String ipActual() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return attributes.getRequest().getRemoteAddr();
        }
        return null;
    }
}
