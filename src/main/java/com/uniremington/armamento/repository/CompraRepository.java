package com.uniremington.armamento.repository;

import com.uniremington.armamento.entity.Compra;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompraRepository extends JpaRepository<Compra, Long> {
    Page<Compra> findByNumeroCompraContainingIgnoreCase(String numero, Pageable pageable);

    java.util.List<Compra> findAllByOrderByFechaCompraDesc();

    List<Compra> findByFechaCompraGreaterThanEqualAndFechaCompraLessThanAndEstadoNot(
            LocalDate desde, LocalDate hasta, String estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Compra c where c.id = :id")
    java.util.Optional<Compra> buscarYBloquear(@Param("id") Long id);
}
