package com.gorkyange.bp.infrastructure.adapter.in.rest;

import com.gorkyange.bp.application.port.in.*;
import com.gorkyange.bp.domain.model.Cuenta;
import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.CuentaRequest;
import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.CuentaResponse;
import com.gorkyange.bp.infrastructure.adapter.in.rest.mapper.CuentaRestMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cuentas")
public class CuentaController {

    private final CrearCuentaUseCase crearCuentaUseCase;
    private final ActualizarCuentaUseCase actualizarCuentaUseCase;
    private final ObtenerCuentaUseCase obtenerCuentaUseCase;
    private final ListarCuentasUseCase listarCuentasUseCase;
    private final EliminarCuentaUseCase eliminarCuentaUseCase;
    private final CuentaRestMapper mapper;

    public CuentaController(CrearCuentaUseCase crearCuentaUseCase,
                            ActualizarCuentaUseCase actualizarCuentaUseCase,
                            ObtenerCuentaUseCase obtenerCuentaUseCase,
                            ListarCuentasUseCase listarCuentasUseCase,
                            EliminarCuentaUseCase eliminarCuentaUseCase,
                            CuentaRestMapper mapper) {
        this.crearCuentaUseCase = crearCuentaUseCase;
        this.actualizarCuentaUseCase = actualizarCuentaUseCase;
        this.obtenerCuentaUseCase = obtenerCuentaUseCase;
        this.listarCuentasUseCase = listarCuentasUseCase;
        this.eliminarCuentaUseCase = eliminarCuentaUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<CuentaResponse> crear(@RequestBody CuentaRequest request) {
        Cuenta cuenta = mapper.toDomain(request);
        Cuenta creada = crearCuentaUseCase.crear(cuenta);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(creada));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponse> obtener(@PathVariable Long id) {
        return obtenerCuentaUseCase.obtenerPorId(id)
                .map(cuenta -> ResponseEntity.ok(mapper.toResponse(cuenta)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CuentaResponse>> listar(@RequestParam(required = false) Long clienteId) {
        List<Cuenta> cuentas;
        if (clienteId != null) {
            cuentas = listarCuentasUseCase.listarPorCliente(clienteId);
        } else {
            cuentas = listarCuentasUseCase.listarTodas();
        }
        
        List<CuentaResponse> response = cuentas.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaResponse> actualizar(@PathVariable Long id,
                                                      @RequestBody CuentaRequest request) {
        Cuenta cuenta = mapper.toDomain(request);
        Cuenta actualizada = actualizarCuentaUseCase.actualizar(id, cuenta);
        return ResponseEntity.ok(mapper.toResponse(actualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        eliminarCuentaUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
