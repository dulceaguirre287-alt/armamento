package com.uniremington.armamento.repository;

import java.util.Optional;

import com.uniremington.armamento.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmailIgnoreCase(String email);

    Page<Usuario> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCaseOrUsernameContainingIgnoreCase(
            String nombres, String apellidos, String username, Pageable pageable);
}
