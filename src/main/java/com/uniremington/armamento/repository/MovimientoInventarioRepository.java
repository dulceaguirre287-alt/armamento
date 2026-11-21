package com.uniremington.armamento.repository;

import com.uniremington.armamento.entity.MovimientoInventario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    List<MovimientoInventario> findTop10ByOrderByFechaDesc();
}
