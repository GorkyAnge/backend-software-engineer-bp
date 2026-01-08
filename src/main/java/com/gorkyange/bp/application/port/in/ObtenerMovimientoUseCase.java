package com.gorkyange.bp.application.port.in;

import com.gorkyange.bp.domain.model.Movimiento;

import java.util.Optional;

public interface ObtenerMovimientoUseCase {
    Optional<Movimiento> obtenerPorId(Long id);
}
