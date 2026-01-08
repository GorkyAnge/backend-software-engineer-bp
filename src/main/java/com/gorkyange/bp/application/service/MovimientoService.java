package com.gorkyange.bp.application.service;

import com.gorkyange.bp.application.port.in.*;
import com.gorkyange.bp.application.port.out.MovimientoRepositoryPort;
import com.gorkyange.bp.domain.model.Cliente;
import com.gorkyange.bp.domain.model.Cuenta;
import com.gorkyange.bp.domain.model.Movimiento;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MovimientoService implements CrearMovimientoUseCase, ListarMovimientosUseCase, 
                                          ObtenerMovimientoUseCase, ActualizarMovimientoUseCase, 
                                          EliminarMovimientoUseCase {

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

    @Override
    public Optional<Movimiento> obtenerPorId(Long id) {
        return movimientoRepository.buscarPorId(id);
    }

    @Override
    public Movimiento actualizar(Long id, Movimiento movimiento) {
        Movimiento existente = movimientoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado: " + id));
        
        // Mantener ID y recalcular saldo si cambia el valor
        movimiento.setId(existente.getId());
        
        if (!movimiento.getValor().equals(existente.getValor()) || 
            !movimiento.getNumeroCuenta().equals(existente.getNumeroCuenta())) {
            // Recalcular saldo
            Cuenta cuenta = obtenerCuentaUseCase.obtenerPorNumeroCuenta(movimiento.getNumeroCuenta())
                    .orElseThrow(() -> new RuntimeException("Cuenta no encontrada: " + movimiento.getNumeroCuenta()));
            
            Double saldoActual = movimientoRepository.buscarUltimoPorCuenta(movimiento.getNumeroCuenta())
                    .map(Movimiento::getSaldo)
                    .orElse(cuenta.getSaldoInicial());
            
            Double nuevoSaldo = saldoActual + movimiento.getValor();
            
            if (movimiento.getValor() < 0 && nuevoSaldo < 0) {
                throw new RuntimeException("Saldo no disponible");
            }
            
            movimiento.setSaldo(nuevoSaldo);
        } else {
            movimiento.setSaldo(existente.getSaldo());
        }
        
        return movimientoRepository.guardar(movimiento);
    }

    @Override
    public void eliminar(Long id) {
        if (!movimientoRepository.buscarPorId(id).isPresent()) {
            throw new RuntimeException("Movimiento no encontrado: " + id);
        }
        movimientoRepository.eliminar(id);
    }
}
