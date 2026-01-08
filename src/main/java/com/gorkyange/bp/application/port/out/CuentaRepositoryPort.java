package com.gorkyange.bp.application.port.out;

import com.gorkyange.bp.domain.model.Cuenta;
import java.util.List;
import java.util.Optional;

public interface CuentaRepositoryPort {
    Cuenta guardar(Cuenta cuenta);
    Optional<Cuenta> buscarPorId(Long id);
    Optional<Cuenta> buscarPorNumeroCuenta(String numeroCuenta);
    List<Cuenta> buscarTodas();
    List<Cuenta> buscarPorCliente(Long clienteId);
    void eliminar(Long id);
    boolean existePorId(Long id);
}
