package com.gorkyange.bp.infrastructure.adapter.out.persistence.repository;

import com.gorkyange.bp.infrastructure.adapter.out.persistence.entity.MovimientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovimientoJpaRepository extends JpaRepository<MovimientoEntity, Long> {
    
    List<MovimientoEntity> findByNumeroCuentaOrderByFechaDesc(String numeroCuenta);
    
    List<MovimientoEntity> findByFechaBetweenOrderByFechaDesc(LocalDate fechaInicio, LocalDate fechaFin);
    
    @Query("SELECT m FROM MovimientoEntity m WHERE m.numeroCuenta IN " +
           "(SELECT c.numeroCuenta FROM CuentaEntity c WHERE c.clienteId = :clienteId) " +
           "AND m.fecha BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY m.fecha DESC")
    List<MovimientoEntity> findByClienteIdAndFechaBetween(
            @Param("clienteId") Long clienteId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);
    
    Optional<MovimientoEntity> findFirstByNumeroCuentaOrderByFechaDescIdDesc(String numeroCuenta);
}
