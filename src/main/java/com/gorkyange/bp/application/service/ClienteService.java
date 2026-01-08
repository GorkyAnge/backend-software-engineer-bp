package com.gorkyange.bp.application.service;

import com.gorkyange.bp.application.port.in.*;
import com.gorkyange.bp.application.port.out.ClienteRepositoryPort;
import com.gorkyange.bp.domain.model.Cliente;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService implements CrearClienteUseCase, ActualizarClienteUseCase,
        ObtenerClienteUseCase, ListarClientesUseCase, EliminarClienteUseCase {

    private final ClienteRepositoryPort clienteRepository;

    public ClienteService(ClienteRepositoryPort clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Cliente crear(Cliente cliente) {
        return clienteRepository.guardar(cliente);
    }

    @Override
    public Cliente actualizar(Long clienteId, Cliente cliente) {
        if (!clienteRepository.existePorId(clienteId)) {
            throw new RuntimeException("Cliente no encontrado con ID: " + clienteId);
        }
        cliente.setClienteId(clienteId);
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
            throw new RuntimeException("Cliente no encontrado con ID: " + clienteId);
        }
        clienteRepository.eliminar(clienteId);
    }
}
