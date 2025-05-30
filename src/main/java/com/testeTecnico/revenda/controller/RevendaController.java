package com.testeTecnico.revenda.controller;

import com.testeTecnico.revenda.model.request.RevendaRequestDTO;
import com.testeTecnico.revenda.model.response.RevendaResponseDTO;
import com.testeTecnico.revenda.service.RevendaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/revenda")
public class RevendaController {

    @Autowired
    private RevendaService revendaService;

    @PostMapping("/criar-revenda")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RevendaResponseDTO> criarRevenda(@RequestBody @Valid RevendaRequestDTO requestDTO){
        return revendaService.criarRevenda(requestDTO);
    }
}
