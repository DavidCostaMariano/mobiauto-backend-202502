package com.testeTecnico.revenda.service;

import com.testeTecnico.revenda.model.ClienteEntity;
import com.testeTecnico.revenda.model.request.ClienteRequestDTO;
import com.testeTecnico.revenda.model.request.VeiculoRequestDTO;
import com.testeTecnico.revenda.model.response.ClienteResponseDTO;
import com.testeTecnico.revenda.repository.ClienteRepository;
import com.testeTecnico.revenda.util.mapper.ClienteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {


    @Autowired
    private ClienteMapper clienteMapper;

    @Autowired
    private ClienteRepository clienteRepository;
    public ResponseEntity<ClienteResponseDTO> criarCliente(ClienteRequestDTO requestDTO) {
        ClienteEntity cliente = clienteMapper.toEntityFromRequestDto(requestDTO);
        clienteRepository.save(cliente);
        return ResponseEntity.ok(
                clienteMapper.toResponseDto("Cliente cadastrado com sucesso", cliente));
    }
}
