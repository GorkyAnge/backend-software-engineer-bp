package com.gorkyange.bp.infrastructure.adapter.in.rest.mapper;

import com.gorkyange.bp.domain.model.Cuenta;
import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.CuentaRequest;
import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.CuentaResponse;
import org.springframework.stereotype.Component;

@Component
public class CuentaRestMapper {

    public Cuenta toDomain(CuentaRequest request) {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(request.getNumeroCuenta());
        cuenta.setTipoCuenta(request.getTipoCuenta());
        cuenta.setSaldoInicial(request.getSaldoInicial());
        cuenta.setEstado(request.getEstado());
        cuenta.setClienteId(request.getClienteId());
        return cuenta;
    }

    public CuentaResponse toResponse(Cuenta cuenta) {
        CuentaResponse response = new CuentaResponse();
        response.setId(cuenta.getId());
        response.setNumeroCuenta(cuenta.getNumeroCuenta());
        response.setTipoCuenta(cuenta.getTipoCuenta());
        response.setSaldoInicial(cuenta.getSaldoInicial());
        response.setEstado(cuenta.getEstado());
        response.setClienteId(cuenta.getClienteId());
        return response;
    }
}
