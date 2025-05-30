package com.testeTecnico.revenda.controller;

import com.testeTecnico.revenda.model.request.ClienteRequestDTO;
import com.testeTecnico.revenda.model.request.VeiculoRequestDTO;
import com.testeTecnico.revenda.model.response.ClienteResponseDTO;
import com.testeTecnico.revenda.model.response.VeiculoResponseDTO;
import com.testeTecnico.revenda.service.ClienteService;
import com.testeTecnico.revenda.service.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cliente")
public class ClienteController {
    @Autowired
    private ClienteService clienteService;

    @PostMapping("/criar-cliente")
    public ResponseEntity<ClienteResponseDTO> criarCliente(@RequestBody ClienteRequestDTO requestDTO){
        return clienteService.criarCliente(requestDTO);
    }

}
