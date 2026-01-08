package com.gorkyange.bp.application.service;

import com.gorkyange.bp.application.port.in.*;
import com.gorkyange.bp.application.port.out.CuentaRepositoryPort;
import com.gorkyange.bp.domain.model.Cuenta;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CuentaService implements CrearCuentaUseCase, ActualizarCuentaUseCase,
        ObtenerCuentaUseCase, ListarCuentasUseCase, EliminarCuentaUseCase {

    private final CuentaRepositoryPort cuentaRepository;

    public CuentaService(CuentaRepositoryPort cuentaRepository) {
        this.cuentaRepository = cuentaRepository;
    }

    @Override
    public Cuenta crear(Cuenta cuenta) {
        return cuentaRepository.guardar(cuenta);
    }

    @Override
    public Cuenta actualizar(Long id, Cuenta cuenta) {
        if (!cuentaRepository.existePorId(id)) {
            throw new RuntimeException("Cuenta no encontrada con ID: " + id);
        }
        cuenta.setId(id);
        return cuentaRepository.guardar(cuenta);
    }

    @Override
    public Optional<Cuenta> obtenerPorId(Long id) {
        return cuentaRepository.buscarPorId(id);
    }

    @Override
    public Optional<Cuenta> obtenerPorNumeroCuenta(String numeroCuenta) {
        return cuentaRepository.buscarPorNumeroCuenta(numeroCuenta);
    }

    @Override
    public List<Cuenta> listarTodas() {
        return cuentaRepository.buscarTodas();
    }

    @Override
    public List<Cuenta> listarPorCliente(Long clienteId) {
        return cuentaRepository.buscarPorCliente(clienteId);
    }

    @Override
    public void eliminar(Long id) {
        if (!cuentaRepository.existePorId(id)) {
            throw new RuntimeException("Cuenta no encontrada con ID: " + id);
        }
        cuentaRepository.eliminar(id);
    }
}
