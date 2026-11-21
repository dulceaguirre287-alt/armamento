package com.uniremington.armamento.repository;

import java.util.List;

import com.uniremington.armamento.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    List<Categoria> findByActivoTrueOrderByNombreAsc();
}
