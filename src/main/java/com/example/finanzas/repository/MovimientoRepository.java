package com.example.finanzas.repository;

import com.example.finanzas.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MovimientoRepository extends JpaRepository<Movimiento, UUID> {}
