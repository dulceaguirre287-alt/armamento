package com.uniremington.armamento.repository;

import java.util.Optional;

import com.uniremington.armamento.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByNombre(String nombre);
}
