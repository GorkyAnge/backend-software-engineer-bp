package com.gorkyange.bp.domain.exception;

public class CuentaDuplicadaException extends RuntimeException {
    public CuentaDuplicadaException(String numeroCuenta) {
        super(String.format("Ya existe una cuenta con el número '%s'", numeroCuenta));
    }
}
