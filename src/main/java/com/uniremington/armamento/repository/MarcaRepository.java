package com.uniremington.armamento.repository;

import java.util.List;

import com.uniremington.armamento.entity.Marca;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarcaRepository extends JpaRepository<Marca, Integer> {
    List<Marca> findByActivoTrueOrderByNombreAsc();
}
