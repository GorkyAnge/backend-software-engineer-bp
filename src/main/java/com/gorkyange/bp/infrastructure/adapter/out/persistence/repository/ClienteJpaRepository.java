package com.gorkyange.bp.infrastructure.adapter.out.persistence.repository;

import com.gorkyange.bp.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, Long> {
    Optional<ClienteEntity> findByClienteId(Long clienteId);
    boolean existsByClienteId(Long clienteId);
    void deleteByClienteId(Long clienteId);
}
