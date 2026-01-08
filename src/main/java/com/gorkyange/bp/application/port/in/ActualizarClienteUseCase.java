package com.gorkyange.bp.application.port.in;

import com.gorkyange.bp.domain.model.Cliente;

public interface ActualizarClienteUseCase {
    Cliente actualizar(Long clienteId, Cliente cliente);
}
