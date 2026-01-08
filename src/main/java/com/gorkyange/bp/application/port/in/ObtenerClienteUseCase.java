package com.gorkyange.bp.application.port.in;

import com.gorkyange.bp.domain.model.Cliente;
import java.util.Optional;

public interface ObtenerClienteUseCase {
    Optional<Cliente> obtenerPorId(Long clienteId);
}
