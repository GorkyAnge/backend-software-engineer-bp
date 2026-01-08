package com.gorkyange.bp.domain.exception;

public class ClienteConCuentasActivasException extends RuntimeException {
    public ClienteConCuentasActivasException(Long clienteId, int cantidadCuentas) {
        super(String.format("No se puede eliminar el cliente con ID %d porque tiene %d cuenta(s) activa(s)", 
              clienteId, cantidadCuentas));
    }
}
