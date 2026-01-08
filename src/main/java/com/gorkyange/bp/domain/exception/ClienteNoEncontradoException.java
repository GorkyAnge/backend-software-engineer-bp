package com.gorkyange.bp.domain.exception;

public class ClienteNoEncontradoException extends RuntimeException {
    public ClienteNoEncontradoException(Long clienteId) {
        super(String.format("El cliente con ID %d no existe en el sistema", clienteId));
    }
    
    public ClienteNoEncontradoException(String identificacion) {
        super(String.format("El cliente con identificación '%s' no existe en el sistema", identificacion));
    }
}
