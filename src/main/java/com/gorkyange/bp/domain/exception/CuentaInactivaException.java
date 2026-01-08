package com.gorkyange.bp.domain.exception;

public class CuentaInactivaException extends RuntimeException {
    public CuentaInactivaException(String numeroCuenta) {
        super(String.format("La cuenta '%s' está inactiva y no puede realizar movimientos", numeroCuenta));
    }
}
