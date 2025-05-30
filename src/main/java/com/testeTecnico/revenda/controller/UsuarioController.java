package com.testeTecnico.revenda.controller;

import com.testeTecnico.revenda.model.request.AuthRequest;
import com.testeTecnico.revenda.model.request.UsuarioRequestDTO;
import com.testeTecnico.revenda.model.response.AuthResponse;
import com.testeTecnico.revenda.model.response.UsuarioResponseDTO;
import com.testeTecnico.revenda.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;


    @PostMapping("/criar-usuario")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@RequestBody UsuarioRequestDTO requestDTO){
        return usuarioService.criarUsuario(requestDTO);
    }

    @PostMapping("/modificar-usuario")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROPRIETARIO')")
    public ResponseEntity<UsuarioResponseDTO> modificarInformacoesUsuario(@RequestBody UsuarioRequestDTO requestDTO, Authentication authentication){
        return usuarioService.modificarInformacoesUsuario(requestDTO, authentication);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthRequest authRequest){
        return usuarioService.login(authRequest);
    }

}
