package com.gorkyange.bp.domain.exception;

public class MovimientoInvalidoException extends RuntimeException {
    public MovimientoInvalidoException(String mensaje) {
        super(mensaje);
    }
    
    public static MovimientoInvalidoException valorCero() {
        return new MovimientoInvalidoException("El valor del movimiento no puede ser cero");
    }
    
    public static MovimientoInvalidoException noEncontrado(Long movimientoId) {
        return new MovimientoInvalidoException(
            String.format("El movimiento con ID %d no existe en el sistema", movimientoId)
        );
    }
}
