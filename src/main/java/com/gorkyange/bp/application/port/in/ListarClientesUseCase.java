package com.gorkyange.bp.application.port.in;

import com.gorkyange.bp.domain.model.Cliente;
import java.util.List;

public interface ListarClientesUseCase {
    List<Cliente> listarTodos();
}
