package com.gorkyange.bp.application.service;

import com.gorkyange.bp.application.port.in.CrearMovimientoUseCase;
import com.gorkyange.bp.application.port.in.ListarMovimientosUseCase;
import com.gorkyange.bp.application.port.in.ObtenerCuentaUseCase;
import com.gorkyange.bp.application.port.out.MovimientoRepositoryPort;
import com.gorkyange.bp.domain.model.Cuenta;
import com.gorkyange.bp.domain.model.Movimiento;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MovimientoService implements CrearMovimientoUseCase, ListarMovimientosUseCase {

    private final MovimientoRepositoryPort movimientoRepository;
    private final ObtenerCuentaUseCase obtenerCuentaUseCase;

    public MovimientoService(MovimientoRepositoryPort movimientoRepository,
                            ObtenerCuentaUseCase obtenerCuentaUseCase) {
        this.movimientoRepository = movimientoRepository;
        this.obtenerCuentaUseCase = obtenerCuentaUseCase;
    }

    @Override
    public Movimiento crear(Movimiento movimiento) {
        // Validar que la cuenta exista
        Cuenta cuenta = obtenerCuentaUseCase.obtenerPorNumeroCuenta(movimiento.getNumeroCuenta())
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada: " + movimiento.getNumeroCuenta()));

        // Obtener el último saldo de la cuenta
        Double saldoActual = movimientoRepository.buscarUltimoPorCuenta(movimiento.getNumeroCuenta())
                .map(Movimiento::getSaldo)
                .orElse(cuenta.getSaldoInicial());

        // Calcular nuevo saldo según el tipo de movimiento
        // Créditos son positivos, débitos son negativos
        Double nuevoSaldo = saldoActual + movimiento.getValor();

        // Validar que el saldo no sea negativo para débitos
        if (movimiento.getValor() < 0 && nuevoSaldo < 0) {
            throw new RuntimeException("Saldo no disponible");
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
        return movimientoRepository.buscarTodos();
    }

    @Override
    public List<Movimiento> listarPorCuenta(String numeroCuenta) {
        return movimientoRepository.buscarPorCuenta(numeroCuenta);
    }

    @Override
    public List<Movimiento> listarPorFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return movimientoRepository.buscarPorFechas(fechaInicio, fechaFin);
    }

    @Override
    public List<Movimiento> listarPorClienteYFechas(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        return movimientoRepository.buscarPorClienteYFechas(clienteId, fechaInicio, fechaFin);
    }
}
