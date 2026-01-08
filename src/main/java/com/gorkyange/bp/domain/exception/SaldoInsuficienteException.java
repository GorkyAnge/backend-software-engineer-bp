package com.gorkyange.bp.domain.exception;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(Double saldoActual, Double valorRetiro) {
        super(String.format("Saldo no disponible. Saldo actual: %.2f, Valor a retirar: %.2f", 
              saldoActual, Math.abs(valorRetiro)));
    }
}
