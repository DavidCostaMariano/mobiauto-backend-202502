package com.testeTecnico.revenda.controller;

import com.testeTecnico.revenda.model.request.AuthRequest;
import com.testeTecnico.revenda.model.request.UsuarioRequestDTO;
import com.testeTecnico.revenda.model.response.AuthResponse;
import com.testeTecnico.revenda.model.response.UsuarioResponseDTO;
import com.testeTecnico.revenda.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Usuário", description = "Endpoints relacionados aos usuários")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;


    @PostMapping("/criar-usuario")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROPRIETARIO', 'GERENTE')")
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário vinculado a uma revenda, somente administradores, proprietarios e gerentes tem acesso a este endpoint")
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@RequestBody UsuarioRequestDTO requestDTO, Authentication auth){
        return usuarioService.criarUsuario(requestDTO, auth);
    }

    @PostMapping("/modificar-usuario")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROPRIETARIO')")
    @Operation(summary = "Modifica um usuário", description = "Cria um novo usuário baseado na request, somente administradores e proprietarios tem acesso a este endpoint")
    public ResponseEntity<UsuarioResponseDTO> modificarInformacoesUsuario(@RequestBody UsuarioRequestDTO requestDTO, Authentication authentication){
        return usuarioService.modificarInformacoesUsuario(requestDTO, authentication);
    }

    @PostMapping("/login")
    @Operation(summary = "Login de usuário", description = "Realiza login e retorna o token JWT, todos os usuarios tem acesso a este endpoint")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest){
        return usuarioService.login(authRequest);
    }

}
