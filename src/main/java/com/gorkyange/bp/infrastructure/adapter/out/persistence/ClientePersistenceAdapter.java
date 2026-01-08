package com.gorkyange.bp.infrastructure.adapter.out.persistence;

import com.gorkyange.bp.application.port.out.ClienteRepositoryPort;
import com.gorkyange.bp.domain.model.Cliente;
import com.gorkyange.bp.infrastructure.adapter.out.persistence.mapper.ClienteMapper;
import com.gorkyange.bp.infrastructure.adapter.out.persistence.repository.ClienteJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ClientePersistenceAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository jpaRepository;
    private final ClienteMapper mapper;

    public ClientePersistenceAdapter(ClienteJpaRepository jpaRepository, ClienteMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(cliente)));
    }

    @Override
    public Optional<Cliente> buscarPorId(Long clienteId) {
        return jpaRepository.findById(clienteId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Cliente> buscarTodos() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminar(Long clienteId) {
        jpaRepository.deleteById(clienteId);
    }

    @Override
    public boolean existePorId(Long clienteId) {
        return jpaRepository.existsById(clienteId);
    }

    @Override
    public boolean existePorIdentificacion(String identificacion) {
        return jpaRepository.existsByIdentificacion(identificacion);
    }
}
