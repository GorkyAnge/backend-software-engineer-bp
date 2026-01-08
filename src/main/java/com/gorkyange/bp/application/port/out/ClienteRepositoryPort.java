package com.gorkyange.bp.application.port.out;

import com.gorkyange.bp.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteRepositoryPort {
    Cliente guardar(Cliente cliente);
    Optional<Cliente> buscarPorId(Long clienteId);
    List<Cliente> buscarTodos();
    void eliminar(Long clienteId);
    boolean existePorId(Long clienteId);
}
