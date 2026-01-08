package com.gorkyange.bp.application.service;

import com.gorkyange.bp.application.port.in.*;
import com.gorkyange.bp.domain.model.Cliente;
import com.gorkyange.bp.domain.model.Cuenta;
import com.gorkyange.bp.domain.model.Movimiento;
import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.ReporteEstadoCuentaResponse;
import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.ReporteEstadoCuentaResponse.CuentaReporte;
import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.ReporteEstadoCuentaResponse.MovimientoReporte;
import com.gorkyange.bp.infrastructure.adapter.in.rest.dto.ReporteEstadoCuentaResponse.ResumenReporte;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReporteService implements GenerarReporteUseCase {

    private final ObtenerClienteUseCase obtenerClienteUseCase;
    private final ListarCuentasUseCase listarCuentasUseCase;
    private final ListarMovimientosUseCase listarMovimientosUseCase;

    public ReporteService(ObtenerClienteUseCase obtenerClienteUseCase,
                         ListarCuentasUseCase listarCuentasUseCase,
                         ListarMovimientosUseCase listarMovimientosUseCase) {
        this.obtenerClienteUseCase = obtenerClienteUseCase;
        this.listarCuentasUseCase = listarCuentasUseCase;
        this.listarMovimientosUseCase = listarMovimientosUseCase;
    }

    @Override
    public ReporteEstadoCuentaResponse generarReporte(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        // Obtener cliente
        Cliente cliente = obtenerClienteUseCase.obtenerPorId(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + clienteId));

        // Obtener cuentas del cliente
        List<Cuenta> cuentas = listarCuentasUseCase.listarPorCliente(clienteId);

        // Inicializar totales
        double totalDebitos = 0;
        double totalCreditos = 0;
        double saldoFinal = 0;

        // Procesar cada cuenta
        List<CuentaReporte> cuentasReporte = new ArrayList<>();
        for (Cuenta cuenta : cuentas) {
            // Obtener movimientos de la cuenta en el rango de fechas
            List<Movimiento> movimientos = listarMovimientosUseCase.listarPorClienteYFechas(clienteId, fechaInicio, fechaFin)
                    .stream()
                    .filter(m -> m.getNumeroCuenta().equals(cuenta.getNumeroCuenta()))
                    .collect(Collectors.toList());

            // Convertir movimientos a DTO
            List<MovimientoReporte> movimientosReporte = movimientos.stream()
                    .map(m -> new MovimientoReporte(
                            m.getFecha(),
                            m.getTipoMovimiento(),
                            m.getValor(),
                            m.getSaldo()
                    ))
                    .collect(Collectors.toList());

            // Calcular saldo actual (último saldo del periodo o saldo inicial si no hay movimientos)
            Double saldoActual = movimientos.isEmpty() 
                    ? cuenta.getSaldoInicial() 
                    : movimientos.get(movimientos.size() - 1).getSaldo();

            // Acumular débitos y créditos
            for (Movimiento m : movimientos) {
                if (m.getValor() < 0) {
                    totalDebitos += Math.abs(m.getValor());
                } else {
                    totalCreditos += m.getValor();
                }
            }

            saldoFinal += saldoActual;

            // Crear reporte de cuenta
            CuentaReporte cuentaReporte = new CuentaReporte(
                    cuenta.getNumeroCuenta(),
                    cuenta.getTipoCuenta(),
                    cuenta.getSaldoInicial(),
                    saldoActual,
                    movimientosReporte
            );
            cuentasReporte.add(cuentaReporte);
        }

        // Crear resumen
        ResumenReporte resumen = new ResumenReporte(totalDebitos, totalCreditos, saldoFinal);

        // Construir respuesta
        return new ReporteEstadoCuentaResponse(
                clienteId,
                cliente.getNombre(),
                fechaInicio,
                fechaFin,
                cuentasReporte,
                resumen
        );
    }

    @Override
    public String generarReportePdf(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            // Generar datos del reporte
            ReporteEstadoCuentaResponse reporte = generarReporte(clienteId, fechaInicio, fechaFin);

            // Crear PDF en memoria
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Título
            document.add(new Paragraph("Estado de Cuenta")
                    .setBold()
                    .setFontSize(18));
            document.add(new Paragraph("Cliente: " + reporte.getNombreCliente()));
            document.add(new Paragraph("Período: " + reporte.getFechaInicio() + " - " + reporte.getFechaFin()));
            document.add(new Paragraph("\n"));

            // Procesar cada cuenta
            for (CuentaReporte cuenta : reporte.getCuentas()) {
                document.add(new Paragraph("Cuenta: " + cuenta.getNumeroCuenta())
                        .setBold());
                document.add(new Paragraph("Tipo: " + cuenta.getTipoCuenta()));
                document.add(new Paragraph("Saldo Inicial: $" + cuenta.getSaldoInicial()));
                document.add(new Paragraph("Saldo Actual: $" + cuenta.getSaldoActual()));
                document.add(new Paragraph("\n"));

                // Tabla de movimientos
                if (!cuenta.getMovimientos().isEmpty()) {
                    Table table = new Table(4);
                    table.addHeaderCell("Fecha");
                    table.addHeaderCell("Tipo");
                    table.addHeaderCell("Valor");
                    table.addHeaderCell("Saldo");

                    for (MovimientoReporte mov : cuenta.getMovimientos()) {
                        table.addCell(mov.getFecha().toString());
                        table.addCell(mov.getTipoMovimiento());
                        table.addCell("$" + mov.getValor());
                        table.addCell("$" + mov.getSaldo());
                    }

                    document.add(table);
                    document.add(new Paragraph("\n"));
                }
            }

            // Resumen
            document.add(new Paragraph("RESUMEN").setBold().setFontSize(14));
            document.add(new Paragraph("Total Débitos: $" + reporte.getResumen().getTotalDebitos()));
            document.add(new Paragraph("Total Créditos: $" + reporte.getResumen().getTotalCreditos()));
            document.add(new Paragraph("Saldo Final: $" + reporte.getResumen().getSaldoFinal()));

            // Cerrar documento
            document.close();

            // Convertir a Base64
            byte[] pdfBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(pdfBytes);

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage(), e);
        }
    }
}
