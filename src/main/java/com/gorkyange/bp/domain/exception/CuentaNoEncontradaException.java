package com.gorkyange.bp.domain.exception;

public class CuentaNoEncontradaException extends RuntimeException {
    public CuentaNoEncontradaException(String numeroCuenta) {
        super(String.format("La cuenta con número '%s' no existe en el sistema", numeroCuenta));
    }
    
    public CuentaNoEncontradaException(Long id) {
        super(String.format("La cuenta con ID %d no existe en el sistema", id));
    }
}
