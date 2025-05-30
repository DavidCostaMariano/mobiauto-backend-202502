package com.testeTecnico.revenda.controller;

import com.testeTecnico.revenda.model.request.UsuarioRequestDTO;
import com.testeTecnico.revenda.model.request.VeiculoRequestDTO;
import com.testeTecnico.revenda.model.response.UsuarioResponseDTO;
import com.testeTecnico.revenda.model.response.VeiculoResponseDTO;
import com.testeTecnico.revenda.service.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/veiculo")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;

    @PostMapping("/criar-veiculo")
    public ResponseEntity<VeiculoResponseDTO> criarVeiculo(@RequestBody VeiculoRequestDTO requestDTO){
        return veiculoService.criarVeiculo(requestDTO);
    }
}
