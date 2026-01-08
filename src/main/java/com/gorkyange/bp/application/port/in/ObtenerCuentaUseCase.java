package com.gorkyange.bp.application.port.in;

import com.gorkyange.bp.domain.model.Cuenta;
import java.util.Optional;

public interface ObtenerCuentaUseCase {
    Optional<Cuenta> obtenerPorId(Long id);
    Optional<Cuenta> obtenerPorNumeroCuenta(String numeroCuenta);
}
