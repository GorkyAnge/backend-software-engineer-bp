package com.gorkyange.bp.application.port.out;

import com.gorkyange.bp.domain.model.Movimiento;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MovimientoRepositoryPort {
    Movimiento guardar(Movimiento movimiento);
    Optional<Movimiento> buscarPorId(Long id);
    List<Movimiento> buscarTodos();
    List<Movimiento> buscarPorCuenta(String numeroCuenta);
    List<Movimiento> buscarPorFechas(LocalDate fechaInicio, LocalDate fechaFin);
    List<Movimiento> buscarPorClienteYFechas(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin);
    Optional<Movimiento> buscarUltimoPorCuenta(String numeroCuenta);
    void eliminar(Long id);
}
