package com.gorkyange.bp.infrastructure.adapter.out.persistence;

import com.gorkyange.bp.application.port.out.CuentaRepositoryPort;
import com.gorkyange.bp.domain.model.Cuenta;
import com.gorkyange.bp.infrastructure.adapter.out.persistence.mapper.CuentaMapper;
import com.gorkyange.bp.infrastructure.adapter.out.persistence.repository.CuentaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CuentaPersistenceAdapter implements CuentaRepositoryPort {

    private final CuentaJpaRepository jpaRepository;
    private final CuentaMapper mapper;

    public CuentaPersistenceAdapter(CuentaJpaRepository jpaRepository, CuentaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Cuenta guardar(Cuenta cuenta) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(cuenta)));
    }

    @Override
    public Optional<Cuenta> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Cuenta> buscarPorNumeroCuenta(String numeroCuenta) {
        return jpaRepository.findByNumeroCuenta(numeroCuenta).map(mapper::toDomain);
    }

    @Override
    public List<Cuenta> buscarTodas() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Cuenta> buscarPorCliente(Long clienteId) {
        return jpaRepository.findByClienteId(clienteId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existePorId(Long id) {
        return jpaRepository.existsById(id);
    }
}
