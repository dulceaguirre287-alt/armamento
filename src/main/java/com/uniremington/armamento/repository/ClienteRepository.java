package com.uniremington.armamento.repository;

import com.uniremington.armamento.entity.Cliente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    List<Cliente> findByActivoTrueOrderByNombreRazonAsc();
}
