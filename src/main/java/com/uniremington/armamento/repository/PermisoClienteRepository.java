package com.uniremington.armamento.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import com.uniremington.armamento.entity.PermisoCliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermisoClienteRepository extends JpaRepository<PermisoCliente, Integer> {
    Optional<PermisoCliente> findFirstByClienteIdAndCategoriaIdAndEstadoAndFechaVencimientoGreaterThanEqual(
            Integer clienteId, Integer categoriaId, String estado, LocalDate fecha);

    List<PermisoCliente> findByClienteIdAndCategoriaIdAndEstadoAndFechaExpedicionLessThanEqualAndFechaVencimientoGreaterThanEqual(
            Integer clienteId, Integer categoriaId, String estado, LocalDate inicio, LocalDate fin);
}
