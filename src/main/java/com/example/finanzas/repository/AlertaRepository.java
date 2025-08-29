package com.example.finanzas.repository;

import com.example.finanzas.entity.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AlertaRepository extends JpaRepository<Alerta, UUID> {}
