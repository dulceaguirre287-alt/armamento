package com.uniremington.armamento.repository;

import com.uniremington.armamento.entity.Devolucion;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DevolucionRepository extends JpaRepository<Devolucion, Long> {
    Page<Devolucion> findByNumeroDevolucionContainingIgnoreCase(String numero, Pageable pageable);

    boolean existsByVenta_IdAndEstadoNot(Long ventaId, String estado);

    boolean existsByCompra_IdAndEstadoNot(Long compraId, String estado);

    long countByFechaDevolucionGreaterThanEqualAndFechaDevolucionLessThanAndEstado(
            LocalDateTime desde, LocalDateTime hasta, String estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Devolucion d where d.id = :id")
    java.util.Optional<Devolucion> buscarYBloquear(@Param("id") Long id);
}
