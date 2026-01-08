package com.gorkyange.bp.infrastructure.adapter.in.rest.exception;

import com.gorkyange.bp.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> handleSaldoInsuficiente(SaldoInsuficienteException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "SALDO_INSUFICIENTE");
    }

    @ExceptionHandler(CuentaNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleCuentaNoEncontrada(CuentaNoEncontradaException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "CUENTA_NO_ENCONTRADA");
    }

    @ExceptionHandler(ClienteNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleClienteNoEncontrado(ClienteNoEncontradoException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "CLIENTE_NO_ENCONTRADO");
    }

    @ExceptionHandler(MovimientoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleMovimientoInvalido(MovimientoInvalidoException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "MOVIMIENTO_INVALIDO");
    }

    @ExceptionHandler(CuentaInactivaException.class)
    public ResponseEntity<Map<String, Object>> handleCuentaInactiva(CuentaInactivaException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "CUENTA_INACTIVA");
    }

    @ExceptionHandler(ClienteDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleClienteDuplicado(ClienteDuplicadoException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), "CLIENTE_DUPLICADO");
    }

    @ExceptionHandler(CuentaDuplicadaException.class)
    public ResponseEntity<Map<String, Object>> handleCuentaDuplicada(CuentaDuplicadaException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), "CUENTA_DUPLICADA");
    }

    @ExceptionHandler(ClienteConCuentasActivasException.class)
    public ResponseEntity<Map<String, Object>> handleClienteConCuentasActivas(ClienteConCuentasActivasException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), "CLIENTE_CON_CUENTAS_ACTIVAS");
    }

    @ExceptionHandler(CuentaConMovimientosException.class)
    public ResponseEntity<Map<String, Object>> handleCuentaConMovimientos(CuentaConMovimientosException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), "CUENTA_CON_MOVIMIENTOS");
    }

    @ExceptionHandler(DatosInvalidosException.class)
    public ResponseEntity<Map<String, Object>> handleDatosInvalidos(DatosInvalidosException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "DATOS_INVALIDOS");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error interno del servidor: " + ex.getMessage(), 
                "ERROR_INTERNO");
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String message, String errorCode) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("errorCode", errorCode);
        
        return new ResponseEntity<>(body, status);
    }
}
