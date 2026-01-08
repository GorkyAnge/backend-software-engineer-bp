package com.gorkyange.bp.infrastructure.adapter.out.persistence.mapper;

import com.gorkyange.bp.domain.model.Movimiento;
import com.gorkyange.bp.infrastructure.adapter.out.persistence.entity.MovimientoEntity;
import org.springframework.stereotype.Component;

@Component
public class MovimientoMapper {

    public MovimientoEntity toEntity(Movimiento movimiento) {
        MovimientoEntity entity = new MovimientoEntity();
        entity.setId(movimiento.getId());
        entity.setFecha(movimiento.getFecha());
        entity.setTipoMovimiento(movimiento.getTipoMovimiento());
        entity.setValor(movimiento.getValor());
        entity.setSaldo(movimiento.getSaldo());
        entity.setNumeroCuenta(movimiento.getNumeroCuenta());
        return entity;
    }

    public Movimiento toDomain(MovimientoEntity entity) {
        Movimiento movimiento = new Movimiento();
        movimiento.setId(entity.getId());
        movimiento.setFecha(entity.getFecha());
        movimiento.setTipoMovimiento(entity.getTipoMovimiento());
        movimiento.setValor(entity.getValor());
        movimiento.setSaldo(entity.getSaldo());
        movimiento.setNumeroCuenta(entity.getNumeroCuenta());
        return movimiento;
    }
}
