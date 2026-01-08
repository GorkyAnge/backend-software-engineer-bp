package com.gorkyange.bp.application.port.in;

import com.gorkyange.bp.domain.model.Movimiento;
import java.time.LocalDate;
import java.util.List;

public interface ListarMovimientosUseCase {
    List<Movimiento> listarTodos();
    List<Movimiento> listarPorCuenta(String numeroCuenta);
    List<Movimiento> listarPorFechas(LocalDate fechaInicio, LocalDate fechaFin);
    List<Movimiento> listarPorClienteYFechas(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin);
}
