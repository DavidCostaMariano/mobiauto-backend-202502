package com.testeTecnico.revenda.controller;

import com.testeTecnico.revenda.model.request.ClienteRequestDTO;
import com.testeTecnico.revenda.model.request.OportunidadeRequestDTO;
import com.testeTecnico.revenda.model.request.TransferenciaOportunidadeRequestDTO;
import com.testeTecnico.revenda.model.response.ClienteResponseDTO;
import com.testeTecnico.revenda.model.response.OportunidadeResponseDTO;
import com.testeTecnico.revenda.service.OportunidadeService;
import com.testeTecnico.revenda.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/oportunidade")
public class OportunidadeController {

    @Autowired
    private OportunidadeService oportunidadeService;

    @Autowired
    private Utils utils;

    @PostMapping("/criar-oportunidade")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROPRIETARIO')")
    public ResponseEntity<OportunidadeResponseDTO> criarOportunidade(@RequestBody OportunidadeRequestDTO requestDTO, Authentication authentication){
        return oportunidadeService.criarOportunidade(requestDTO, authentication);
    }

    @GetMapping("/listar-oportunidade/revenda/{id}")
    public ResponseEntity<List<OportunidadeResponseDTO>> listarOportunidades(@PathVariable("id") long revendaId, Authentication authentication){
        return oportunidadeService.listarOportunidades(revendaId, authentication);
    }

    @PutMapping("/alterar-status-oportunidade")
    public ResponseEntity<OportunidadeResponseDTO> alterarStatusOportunidade(@RequestBody OportunidadeRequestDTO requestDTO, Authentication authentication){
        return oportunidadeService.alterarStatusOportunidade(requestDTO, authentication);
    }

    @PutMapping("/transferir")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PROPRIETARIO', 'GERENTE')")
    public ResponseEntity<OportunidadeResponseDTO> transferirOportunidade(@RequestBody TransferenciaOportunidadeRequestDTO requestDTO, Authentication authentication){
        return oportunidadeService.transferirOportunidade(requestDTO, authentication);
    }


    @PutMapping("/modificar-oportunidade")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PROPRIETARIO', 'GERENTE', 'ASSISTENTE')")
    public ResponseEntity<OportunidadeResponseDTO> modificarOportunidade(@RequestBody OportunidadeRequestDTO requestDTO, Authentication authentication){
        return oportunidadeService.modificarOportunidade(requestDTO, authentication);
    }
}
