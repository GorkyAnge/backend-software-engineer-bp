package com.gorkyange.bp.domain.exception;

public class CuentaConMovimientosException extends RuntimeException {
    public CuentaConMovimientosException(String numeroCuenta, int cantidadMovimientos) {
        super(String.format("No se puede eliminar la cuenta '%s' porque tiene %d movimiento(s) registrado(s)", 
              numeroCuenta, cantidadMovimientos));
    }
}
