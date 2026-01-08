package com.gorkyange.bp.application.port.in;

import com.gorkyange.bp.domain.model.Movimiento;

public interface ActualizarMovimientoUseCase {
    Movimiento actualizar(Long id, Movimiento movimiento);
}
