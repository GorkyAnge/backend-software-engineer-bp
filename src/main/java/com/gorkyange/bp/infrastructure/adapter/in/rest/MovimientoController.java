package com.gorkyange.bp.infrastructure.adapter.in.rest;

import com.gorkyange.bp.application.port.in.CrearMovimientoUseCase;
import com.gorkyange.bp.application.port.in.ListarMovimientosUseCase;
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
    private final MovimientoRestMapper mapper;

    public MovimientoController(CrearMovimientoUseCase crearMovimientoUseCase,
                                ListarMovimientosUseCase listarMovimientosUseCase,
                                MovimientoRestMapper mapper) {
        this.crearMovimientoUseCase = crearMovimientoUseCase;
        this.listarMovimientosUseCase = listarMovimientosUseCase;
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
}
