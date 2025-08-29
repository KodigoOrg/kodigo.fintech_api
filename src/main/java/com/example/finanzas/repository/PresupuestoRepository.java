package com.example.finanzas.repository;

import com.example.finanzas.entity.Presupuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PresupuestoRepository extends JpaRepository<Presupuesto, UUID> {}
