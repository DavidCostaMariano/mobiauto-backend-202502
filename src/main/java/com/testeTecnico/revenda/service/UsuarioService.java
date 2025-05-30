package com.testeTecnico.revenda.service;

import com.testeTecnico.revenda.config.security.TokenService;
import com.testeTecnico.revenda.model.RevendaEntity;
import com.testeTecnico.revenda.model.UsuarioEntity;
import com.testeTecnico.revenda.model.request.AuthRequest;
import com.testeTecnico.revenda.model.request.UsuarioRequestDTO;
import com.testeTecnico.revenda.model.response.AuthResponse;
import com.testeTecnico.revenda.model.response.UsuarioResponseDTO;
import com.testeTecnico.revenda.repository.RevendaRepository;
import com.testeTecnico.revenda.repository.UsuarioRepository;
import com.testeTecnico.revenda.util.Utils;
import com.testeTecnico.revenda.util.mapper.UsuarioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {


    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RevendaRepository revendaRepository;


    @Autowired
    private AuthenticationManager authenticationManager;



    @Autowired
    private TokenService tokenService;


    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private Utils utils;

    /* TODO - Precisa verificar se o usuario é administrador, proprietario ou gerente
       TODO - se for adm ele pode cadastrar usuario em qualquer revenda
       TODO - caso não, pode cadastrar somente na própria revenda
     */
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(UsuarioRequestDTO requestDTO) {
        try {
            usuarioAtendeRequisitos(requestDTO);
            RevendaEntity revendaEntity = utils.revendaValido(requestDTO.revenda_id());
            UsuarioEntity usuarioEntity = usuarioMapper.toEntityFromRequestDto(requestDTO, revendaEntity);
            usuarioEntity.setSenha(new BCryptPasswordEncoder().encode(requestDTO.senha()));
            usuarioRepository.save(usuarioEntity);
            return ResponseEntity.ok(
                    usuarioMapper.toResponseDto("Usuario cadastrado com sucesso", usuarioEntity));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(usuarioMapper.toResponseDto(e.getMessage(), new UsuarioEntity()));
        }
    }

    public ResponseEntity<UsuarioResponseDTO> modificarInformacoesUsuario(UsuarioRequestDTO requestDTO, Authentication auth){
        try{
            UsuarioEntity usuarioEntity = utils.usuarioValido(requestDTO.id());
            if(utils.getUserRole(auth).equals("ROLE_PROPRIETARIO")){
                Long revendaId = (Long) auth.getDetails();
                if(usuarioEntity.getRevenda().getId() == revendaId){
                    return atualizarInformacoesDoUsuario(usuarioEntity, requestDTO);
                }
                return ResponseEntity.badRequest().body(usuarioMapper.toResponseDto("Este usuario não pertence à sua loja", new UsuarioEntity()));
            }

            return atualizarInformacoesDoUsuario(usuarioEntity, requestDTO);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(usuarioMapper.toResponseDto(e.getMessage(), new UsuarioEntity()));
        }
    }

    public ResponseEntity<UsuarioResponseDTO> atualizarInformacoesDoUsuario(UsuarioEntity usuarioEntity, UsuarioRequestDTO requestDTO){
        if(usuarioEntity.getCargo().equals("ADMINISTRADOR") && !requestDTO.cargo().equals("ADMINISTRADOR")){
            return ResponseEntity.ok(usuarioMapper.toResponseDto("Você não pode alterar informações de um administrador", usuarioEntity));
        }
        usuarioEntity = usuarioMapper.toEntityFromRequestDto(requestDTO, usuarioEntity.getRevenda());
        usuarioRepository.save(usuarioEntity);
        return ResponseEntity.ok(usuarioMapper.toResponseDto("Usuario modificado com sucesso", usuarioEntity));
    }

    public ResponseEntity<AuthResponse> login(AuthRequest authRequest){

        UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.senha());
        Authentication auth = this.authenticationManager.authenticate(credentials);

        String token = tokenService.generateToken((UsuarioEntity) auth.getPrincipal());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    public boolean usuarioAtendeRequisitos(UsuarioRequestDTO requestDTO) throws Exception {
        if(usuarioRepository.findByEmailIgnoreCase(requestDTO.email()).isEmpty()) return true;
        throw new Exception("Email já presente na base");
    }
}
