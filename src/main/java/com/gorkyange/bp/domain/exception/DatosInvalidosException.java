package com.gorkyange.bp.domain.exception;

public class DatosInvalidosException extends RuntimeException {
    
    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
    
    public static DatosInvalidosException edadInvalida(Integer edad) {
        return new DatosInvalidosException(
            String.format("La edad %d no es válida. Debe ser mayor a 0 y menor a 150", edad)
        );
    }
    
    public static DatosInvalidosException saldoInicialNegativo(Double saldo) {
        return new DatosInvalidosException(
            String.format("El saldo inicial no puede ser negativo: %.2f", saldo)
        );
    }
    
    public static DatosInvalidosException campoRequerido(String campo) {
        return new DatosInvalidosException(
            String.format("El campo '%s' es requerido y no puede estar vacío", campo)
        );
    }
    
    public static DatosInvalidosException contrasenaDebil() {
        return new DatosInvalidosException(
            "La contraseña debe tener al menos 4 caracteres"
        );
    }
    
    public static DatosInvalidosException numeroCuentaInvalido(String numeroCuenta) {
        return new DatosInvalidosException(
            String.format("El número de cuenta '%s' no tiene un formato válido", numeroCuenta)
        );
    }
    
    public static DatosInvalidosException identificacionInvalida(String identificacion) {
        return new DatosInvalidosException(
            String.format("La identificación '%s' no tiene un formato válido", identificacion)
        );
    }
}
