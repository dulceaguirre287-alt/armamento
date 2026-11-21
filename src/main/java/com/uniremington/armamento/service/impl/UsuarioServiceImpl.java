package com.uniremington.armamento.service.impl;

import com.uniremington.armamento.dto.UsuarioFormularioDto;
import com.uniremington.armamento.dto.UsuarioVistaDto;
import com.uniremington.armamento.entity.Rol;
import com.uniremington.armamento.entity.Usuario;
import com.uniremington.armamento.exception.RecursoNoEncontradoException;
import com.uniremington.armamento.mapper.UsuarioMapper;
import com.uniremington.armamento.repository.RolRepository;
import com.uniremington.armamento.repository.UsuarioRepository;
import com.uniremington.armamento.service.AuditoriaService;
import com.uniremington.armamento.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioVistaDto> buscar(String consulta, Pageable pageable) {
        String termino = consulta == null ? "" : consulta.trim();
        Page<Usuario> usuarios = termino.isEmpty()
                ? usuarioRepository.findAll(pageable)
                : usuarioRepository
                        .findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCaseOrUsernameContainingIgnoreCase(
                                termino, termino, termino, pageable);
        return usuarios.map(usuarioMapper::aVista);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioVistaDto obtener(Integer id) {
        return usuarioMapper.aVista(usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", id)));
    }

    @Override
    @Transactional
    public UsuarioVistaDto crear(UsuarioFormularioDto dto) {
        if (dto.getPassword() == null || dto.getPassword().length() < 12) {
            throw new IllegalArgumentException("La contraseña inicial debe tener al menos 12 caracteres.");
        }
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está registrado.");
        }
        validarCorreoDisponible(dto.getEmail(), null);
        Usuario usuario = new Usuario();
        copiarDatos(usuario, dto);
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        UsuarioVistaDto resultado = usuarioMapper.aVista(usuarioRepository.save(usuario));
        auditoriaService.registrar("CREAR", "usuario", resultado.getId(), null, resultado);
        return resultado;
    }

    @Override
    @Transactional
    public UsuarioVistaDto actualizar(Integer id, UsuarioFormularioDto dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", id));
        UsuarioVistaDto antes = usuarioMapper.aVista(usuario);
        if (!usuario.getUsername().equals(dto.getUsername())
                && usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está registrado.");
        }
        validarCorreoDisponible(dto.getEmail(), id);
        copiarDatos(usuario, dto);
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            if (dto.getPassword().length() < 12) {
                throw new IllegalArgumentException("La nueva contraseña debe tener al menos 12 caracteres.");
            }
            usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }
        UsuarioVistaDto resultado = usuarioMapper.aVista(usuarioRepository.save(usuario));
        auditoriaService.registrar("ACTUALIZAR", "usuario", id, antes, resultado);
        return resultado;
    }

    @Override
    @Transactional
    public void desactivar(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", id));
        if (usuario.getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            throw new IllegalArgumentException("No puedes desactivar tu propia cuenta.");
        }
        UsuarioVistaDto antes = usuarioMapper.aVista(usuario);
        usuario.setActivo(false);
        UsuarioVistaDto despues = usuarioMapper.aVista(usuarioRepository.save(usuario));
        auditoriaService.registrar("DESACTIVAR", "usuario", id, antes, despues);
    }

    private void copiarDatos(Usuario usuario, UsuarioFormularioDto dto) {
        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol", dto.getRolId()));
        usuario.setNombres(dto.getNombres().trim());
        usuario.setApellidos(dto.getApellidos().trim());
        usuario.setEmail(dto.getEmail().trim());
        usuario.setUsername(dto.getUsername().trim());
        usuario.setRol(rol);
        usuario.setActivo(dto.isActivo());
    }

    private void validarCorreoDisponible(String email, Integer idActual) {
        if (usuarioRepository.existsByEmailIgnoreCase(email.trim())
                && (idActual == null || !usuarioRepository.findById(idActual)
                        .map(usuario -> usuario.getEmail().equalsIgnoreCase(email.trim()))
                        .orElse(false))) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }
    }
}
