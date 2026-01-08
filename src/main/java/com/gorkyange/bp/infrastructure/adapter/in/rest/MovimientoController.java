package com.gorkyange.bp.infrastructure.adapter.in.rest;

import com.gorkyange.bp.application.port.in.*;
import com.gorkyange.bp.domain.model.Movimiento;
import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.MovimientoRequest;
import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.MovimientoResponse;
import com.gorkyange.bp.infrastructure.adapter.in.rest.mapper.MovimientoRestMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/movimientos")
public class MovimientoController {

    private final CrearMovimientoUseCase crearMovimientoUseCase;
    private final ListarMovimientosUseCase listarMovimientosUseCase;
    private final ObtenerMovimientoUseCase obtenerMovimientoUseCase;
    private final ActualizarMovimientoUseCase actualizarMovimientoUseCase;
    private final EliminarMovimientoUseCase eliminarMovimientoUseCase;
    private final MovimientoRestMapper mapper;

    public MovimientoController(CrearMovimientoUseCase crearMovimientoUseCase,
                                ListarMovimientosUseCase listarMovimientosUseCase,
                                ObtenerMovimientoUseCase obtenerMovimientoUseCase,
                                ActualizarMovimientoUseCase actualizarMovimientoUseCase,
                                EliminarMovimientoUseCase eliminarMovimientoUseCase,
                                MovimientoRestMapper mapper) {
        this.crearMovimientoUseCase = crearMovimientoUseCase;
        this.listarMovimientosUseCase = listarMovimientosUseCase;
        this.obtenerMovimientoUseCase = obtenerMovimientoUseCase;
        this.actualizarMovimientoUseCase = actualizarMovimientoUseCase;
        this.eliminarMovimientoUseCase = eliminarMovimientoUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<MovimientoResponse> crear(@RequestBody MovimientoRequest request) {
        Movimiento movimiento = mapper.toDomain(request);
        Movimiento creado = crearMovimientoUseCase.crear(movimiento);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(creado));
    }

    @GetMapping
    public ResponseEntity<List<MovimientoResponse>> listar(
            @RequestParam(required = false) String numeroCuenta,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        List<Movimiento> movimientos;
        
        if (numeroCuenta != null) {
            movimientos = listarMovimientosUseCase.listarPorCuenta(numeroCuenta);
        } else if (fechaInicio != null && fechaFin != null) {
            movimientos = listarMovimientosUseCase.listarPorFechas(fechaInicio, fechaFin);
        } else {
            movimientos = listarMovimientosUseCase.listarTodos();
        }
        
        List<MovimientoResponse> response = movimientos.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoResponse> obtener(@PathVariable Long id) {
        return obtenerMovimientoUseCase.obtenerPorId(id)
                .map(movimiento -> ResponseEntity.ok(mapper.toResponse(movimiento)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimientoResponse> actualizar(@PathVariable Long id,
                                                          @RequestBody MovimientoRequest request) {
        try {
            Movimiento movimiento = mapper.toDomain(request);
            Movimiento actualizado = actualizarMovimientoUseCase.actualizar(id, movimiento);
            return ResponseEntity.ok(mapper.toResponse(actualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            eliminarMovimientoUseCase.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
