package com.gorkyange.bp.infrastructure.adapter.out.persistence.repository;

import com.gorkyange.bp.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, Long> {
    boolean existsByIdentificacion(String identificacion);
}
