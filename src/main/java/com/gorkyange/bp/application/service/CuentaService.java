package com.gorkyange.bp.application.service;

import com.gorkyange.bp.application.port.in.*;
import com.gorkyange.bp.application.port.out.CuentaRepositoryPort;
import com.gorkyange.bp.application.port.out.MovimientoRepositoryPort;
import com.gorkyange.bp.domain.exception.*;
import com.gorkyange.bp.domain.model.Cuenta;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CuentaService implements CrearCuentaUseCase, ActualizarCuentaUseCase,
        ObtenerCuentaUseCase, ListarCuentasUseCase, EliminarCuentaUseCase {

    private final CuentaRepositoryPort cuentaRepository;
    private final MovimientoRepositoryPort movimientoRepository;

    public CuentaService(CuentaRepositoryPort cuentaRepository, MovimientoRepositoryPort movimientoRepository) {
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Override
    public Cuenta crear(Cuenta cuenta) {
        // Validar datos básicos
        validarDatosCuenta(cuenta);
        
        // Validar duplicados
        if (cuentaRepository.existePorNumeroCuenta(cuenta.getNumeroCuenta())) {
            throw new CuentaDuplicadaException(cuenta.getNumeroCuenta());
        }
        
        return cuentaRepository.guardar(cuenta);
    }

    @Override
    public Cuenta actualizar(Long id, Cuenta cuenta) {
        if (!cuentaRepository.existePorId(id)) {
            throw new CuentaNoEncontradaException(id);
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
        // Buscar la cuenta para obtener el numeroCuenta
        Cuenta cuenta = cuentaRepository.buscarPorId(id)
                .orElseThrow(() -> new CuentaNoEncontradaException(id));
        
        // Validar que no tenga movimientos
        int movimientos = movimientoRepository.contarPorCuenta(cuenta.getNumeroCuenta());
        if (movimientos > 0) {
            throw new CuentaConMovimientosException(cuenta.getNumeroCuenta(), movimientos);
        }
        
        cuentaRepository.eliminar(id);
    }
    
    private void validarDatosCuenta(Cuenta cuenta) {
        if (cuenta.getNumeroCuenta() == null || cuenta.getNumeroCuenta().trim().isEmpty()) {
            throw DatosInvalidosException.campoRequerido("numeroCuenta");
        }
        if (cuenta.getTipoCuenta() == null || cuenta.getTipoCuenta().trim().isEmpty()) {
            throw DatosInvalidosException.campoRequerido("tipoCuenta");
        }
        if (cuenta.getSaldoInicial() == null || cuenta.getSaldoInicial() < 0) {
            throw DatosInvalidosException.saldoInicialNegativo(cuenta.getSaldoInicial());
        }
        if (cuenta.getClienteId() == null) {
            throw DatosInvalidosException.campoRequerido("clienteId");
        }
    }
}
