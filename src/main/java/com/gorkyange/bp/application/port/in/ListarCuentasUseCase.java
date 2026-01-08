package com.gorkyange.bp.application.port.in;

import com.gorkyange.bp.domain.model.Cuenta;
import java.util.List;

public interface ListarCuentasUseCase {
    List<Cuenta> listarTodas();
    List<Cuenta> listarPorCliente(Long clienteId);
}
