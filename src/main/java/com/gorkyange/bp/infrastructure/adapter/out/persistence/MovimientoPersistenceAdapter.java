package com.gorkyange.bp.infrastructure.adapter.out.persistence;

import com.gorkyange.bp.application.port.out.MovimientoRepositoryPort;
import com.gorkyange.bp.domain.model.Movimiento;
import com.gorkyange.bp.infrastructure.adapter.out.persistence.mapper.MovimientoMapper;
import com.gorkyange.bp.infrastructure.adapter.out.persistence.repository.MovimientoJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MovimientoPersistenceAdapter implements MovimientoRepositoryPort {

    private final MovimientoJpaRepository jpaRepository;
    private final MovimientoMapper mapper;

    public MovimientoPersistenceAdapter(MovimientoJpaRepository jpaRepository, MovimientoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Movimiento guardar(Movimiento movimiento) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(movimiento)));
    }

    @Override
    public Optional<Movimiento> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Movimiento> buscarTodos() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movimiento> buscarPorCuenta(String numeroCuenta) {
        return jpaRepository.findByNumeroCuentaOrderByFechaDesc(numeroCuenta)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movimiento> buscarPorFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return jpaRepository.findByFechaBetweenOrderByFechaDesc(fechaInicio, fechaFin)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movimiento> buscarPorClienteYFechas(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        return jpaRepository.findByClienteIdAndFechaBetween(clienteId, fechaInicio, fechaFin)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Movimiento> buscarUltimoPorCuenta(String numeroCuenta) {
        return jpaRepository.findFirstByNumeroCuentaOrderByFechaDescIdDesc(numeroCuenta)
                .map(mapper::toDomain);
    }

    @Override
    public int contarPorCuenta(String numeroCuenta) {
        return jpaRepository.countByNumeroCuenta(numeroCuenta);
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }
}
