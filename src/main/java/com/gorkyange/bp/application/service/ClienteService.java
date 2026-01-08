package com.gorkyange.bp.application.service;

import com.gorkyange.bp.application.port.in.*;
import com.gorkyange.bp.application.port.out.ClienteRepositoryPort;
import com.gorkyange.bp.application.port.out.CuentaRepositoryPort;
import com.gorkyange.bp.domain.exception.*;
import com.gorkyange.bp.domain.model.Cliente;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService implements CrearClienteUseCase, ActualizarClienteUseCase,
        ObtenerClienteUseCase, ListarClientesUseCase, EliminarClienteUseCase {

    private final ClienteRepositoryPort clienteRepository;
    private final CuentaRepositoryPort cuentaRepository;

    public ClienteService(ClienteRepositoryPort clienteRepository, CuentaRepositoryPort cuentaRepository) {
        this.clienteRepository = clienteRepository;
        this.cuentaRepository = cuentaRepository;
    }

    @Override
    public Cliente crear(Cliente cliente) {
        // Validar datos básicos
        validarDatosCliente(cliente);
        
        // Validar duplicados
        if (clienteRepository.existePorIdentificacion(cliente.getIdentificacion())) {
            throw new ClienteDuplicadoException(cliente.getIdentificacion());
        }
        
        return clienteRepository.guardar(cliente);
    }

    @Override
    public Cliente actualizar(Long clienteId, Cliente cliente) {
        if (!clienteRepository.existePorId(clienteId)) {
            throw new ClienteNoEncontradoException(clienteId);
        }
        cliente.setId(clienteId);
        return clienteRepository.guardar(cliente);
    }

    @Override
    public Optional<Cliente> obtenerPorId(Long clienteId) {
        return clienteRepository.buscarPorId(clienteId);
    }

    @Override
    public List<Cliente> listarTodos() {
        return clienteRepository.buscarTodos();
    }

    @Override
    public void eliminar(Long clienteId) {
        if (!clienteRepository.existePorId(clienteId)) {
            throw new ClienteNoEncontradoException(clienteId);
        }
        
        // Validar que no tenga cuentas activas
        int cuentasActivas = cuentaRepository.contarPorCliente(clienteId);
        if (cuentasActivas > 0) {
            throw new ClienteConCuentasActivasException(clienteId, cuentasActivas);
        }
        
        clienteRepository.eliminar(clienteId);
    }
    
    private void validarDatosCliente(Cliente cliente) {
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw DatosInvalidosException.campoRequerido("nombre");
        }
        if (cliente.getIdentificacion() == null || cliente.getIdentificacion().trim().isEmpty()) {
            throw DatosInvalidosException.campoRequerido("identificacion");
        }
        if (cliente.getEdad() == null || cliente.getEdad() < 18 || cliente.getEdad() > 120) {
            throw DatosInvalidosException.edadInvalida(cliente.getEdad());
        }
        if (cliente.getContrasena() == null || cliente.getContrasena().length() < 4) {
            throw DatosInvalidosException.contrasenaDebil();
        }
    }
}
