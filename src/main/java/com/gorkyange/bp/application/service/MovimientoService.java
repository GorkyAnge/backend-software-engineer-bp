package com.gorkyange.bp.application.service;

import com.gorkyange.bp.application.port.in.*;
import com.gorkyange.bp.application.port.out.MovimientoRepositoryPort;
import com.gorkyange.bp.domain.exception.CuentaInactivaException;
import com.gorkyange.bp.domain.exception.CuentaNoEncontradaException;
import com.gorkyange.bp.domain.exception.MovimientoInvalidoException;
import com.gorkyange.bp.domain.exception.SaldoInsuficienteException;
import com.gorkyange.bp.domain.model.Cliente;
import com.gorkyange.bp.domain.model.Cuenta;
import com.gorkyange.bp.domain.model.Movimiento;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MovimientoService implements CrearMovimientoUseCase, ListarMovimientosUseCase {

    private final MovimientoRepositoryPort movimientoRepository;
    private final ObtenerCuentaUseCase obtenerCuentaUseCase;
    private final ObtenerClienteUseCase obtenerClienteUseCase;

    public MovimientoService(MovimientoRepositoryPort movimientoRepository,
                            ObtenerCuentaUseCase obtenerCuentaUseCase,
                            ObtenerClienteUseCase obtenerClienteUseCase) {
        this.movimientoRepository = movimientoRepository;
        this.obtenerCuentaUseCase = obtenerCuentaUseCase;
        this.obtenerClienteUseCase = obtenerClienteUseCase;
    }

    @Override
    public Movimiento crear(Movimiento movimiento) {
        // Validar que el valor no sea cero
        if (movimiento.getValor() == 0) {
            throw MovimientoInvalidoException.valorCero();
        }

        // Validar que la cuenta exista
        Cuenta cuenta = obtenerCuentaUseCase.obtenerPorNumeroCuenta(movimiento.getNumeroCuenta())
                .orElseThrow(() -> new CuentaNoEncontradaException(movimiento.getNumeroCuenta()));

        // Validar que la cuenta esté activa
        if (!cuenta.getEstado()) {
            throw new CuentaInactivaException(movimiento.getNumeroCuenta());
        }

        // Obtener el último saldo de la cuenta
        Double saldoActual = movimientoRepository.buscarUltimoPorCuenta(movimiento.getNumeroCuenta())
                .map(Movimiento::getSaldo)
                .orElse(cuenta.getSaldoInicial());

        // Calcular nuevo saldo según el tipo de movimiento
        // Créditos son positivos, débitos son negativos
        Double nuevoSaldo = saldoActual + movimiento.getValor();

        // Validar que el saldo no sea negativo para débitos (retiros)
        if (movimiento.getValor() < 0 && nuevoSaldo < 0) {
            throw new SaldoInsuficienteException(saldoActual, movimiento.getValor());
        }

        // Establecer el saldo calculado y la fecha si no viene
        movimiento.setSaldo(nuevoSaldo);
        if (movimiento.getFecha() == null) {
            movimiento.setFecha(LocalDate.now());
        }

        return movimientoRepository.guardar(movimiento);
    }

    @Override
    public List<Movimiento> listarTodos() {
        List<Movimiento> movimientos = movimientoRepository.buscarTodos();
        return enriquecerMovimientos(movimientos);
    }

    @Override
    public List<Movimiento> listarPorCuenta(String numeroCuenta) {
        List<Movimiento> movimientos = movimientoRepository.buscarPorCuenta(numeroCuenta);
        return enriquecerMovimientos(movimientos);
    }

    @Override
    public List<Movimiento> listarPorFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Movimiento> movimientos = movimientoRepository.buscarPorFechas(fechaInicio, fechaFin);
        return enriquecerMovimientos(movimientos);
    }

    @Override
    public List<Movimiento> listarPorClienteYFechas(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        List<Movimiento> movimientos = movimientoRepository.buscarPorClienteYFechas(clienteId, fechaInicio, fechaFin);
        return enriquecerMovimientos(movimientos);
    }

    private List<Movimiento> enriquecerMovimientos(List<Movimiento> movimientos) {
        for (Movimiento movimiento : movimientos) {
            try {
                // Obtener información de la cuenta
                Cuenta cuenta = obtenerCuentaUseCase.obtenerPorNumeroCuenta(movimiento.getNumeroCuenta())
                        .orElse(null);
                
                if (cuenta != null) {
                    movimiento.setTipoCuenta(cuenta.getTipoCuenta());
                    movimiento.setSaldoInicial(cuenta.getSaldoInicial());
                    movimiento.setEstadoCuenta(cuenta.getEstado());
                    
                    // Obtener información del cliente
                    if (cuenta.getClienteId() != null) {
                        Cliente cliente = obtenerClienteUseCase.obtenerPorId(cuenta.getClienteId())
                                .orElse(null);
                        if (cliente != null) {
                            movimiento.setNombreCliente(cliente.getNombre());
                        }
                    }
                }
            } catch (Exception e) {
                // Si no se puede enriquecer, continuar con los datos básicos
            }
        }
        return movimientos;
    }
}
