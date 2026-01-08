package com.gorkyange.bp.infrastructure.adapter.out.persistence.mapper;

import com.gorkyange.bp.domain.model.Cuenta;
import com.gorkyange.bp.infrastructure.adapter.out.persistence.entity.CuentaEntity;
import org.springframework.stereotype.Component;

@Component
public class CuentaMapper {

    public CuentaEntity toEntity(Cuenta cuenta) {
        CuentaEntity entity = new CuentaEntity();
        entity.setId(cuenta.getId());
        entity.setNumeroCuenta(cuenta.getNumeroCuenta());
        entity.setTipoCuenta(cuenta.getTipoCuenta());
        entity.setSaldoInicial(cuenta.getSaldoInicial());
        entity.setEstado(cuenta.getEstado());
        entity.setClienteId(cuenta.getClienteId());
        return entity;
    }

    public Cuenta toDomain(CuentaEntity entity) {
        Cuenta cuenta = new Cuenta();
        cuenta.setId(entity.getId());
        cuenta.setNumeroCuenta(entity.getNumeroCuenta());
        cuenta.setTipoCuenta(entity.getTipoCuenta());
        cuenta.setSaldoInicial(entity.getSaldoInicial());
        cuenta.setEstado(entity.getEstado());
        cuenta.setClienteId(entity.getClienteId());
        return cuenta;
    }
}
