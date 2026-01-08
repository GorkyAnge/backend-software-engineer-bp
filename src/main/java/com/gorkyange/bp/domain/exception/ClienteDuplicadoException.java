package com.gorkyange.bp.domain.exception;

public class ClienteDuplicadoException extends RuntimeException {
    public ClienteDuplicadoException(String identificacion) {
        super(String.format("Ya existe un cliente con la identificación '%s'", identificacion));
    }
}
