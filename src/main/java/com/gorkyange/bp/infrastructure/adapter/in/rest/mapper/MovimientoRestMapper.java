package com.gorkyange.bp.infrastructure.adapter.in.rest.mapper;

import com.gorkyange.bp.domain.model.Movimiento;
import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.MovimientoRequest;
import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.MovimientoResponse;
import org.springframework.stereotype.Component;

@Component
public class MovimientoRestMapper {

    public Movimiento toDomain(MovimientoRequest request) {
        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(request.getFecha());
        movimiento.setTipoMovimiento(request.getTipoMovimiento());
        movimiento.setValor(request.getValor());
        movimiento.setNumeroCuenta(request.getNumeroCuenta());
        return movimiento;
    }

    public MovimientoResponse toResponse(Movimiento movimiento) {
        MovimientoResponse response = new MovimientoResponse();
        response.setId(movimiento.getId());
        response.setFecha(movimiento.getFecha());
        response.setTipoMovimiento(movimiento.getTipoMovimiento());
        response.setValor(movimiento.getValor());
        response.setSaldo(movimiento.getSaldo());
        response.setNumeroCuenta(movimiento.getNumeroCuenta());
        response.setCliente(movimiento.getNombreCliente());
        response.setTipoCuenta(movimiento.getTipoCuenta());
        response.setSaldoInicial(movimiento.getSaldoInicial());
        response.setEstado(movimiento.getEstadoCuenta());
        return response;
    }
}
