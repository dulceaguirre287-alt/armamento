package com.uniremington.armamento.repository;

import com.uniremington.armamento.entity.Venta;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    Page<Venta> findByNumeroVentaContainingIgnoreCase(String numero, Pageable pageable);

    java.util.List<Venta> findAllByOrderByFechaVentaDesc();

    List<Venta> findByFechaVentaGreaterThanEqualAndFechaVentaLessThanAndEstadoNot(
            LocalDateTime desde, LocalDateTime hasta, String estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from Venta v where v.id = :id")
    java.util.Optional<Venta> buscarYBloquear(@Param("id") Long id);
}
