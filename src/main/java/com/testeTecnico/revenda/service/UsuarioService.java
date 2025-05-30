package com.testeTecnico.revenda.service;

import com.testeTecnico.revenda.config.security.TokenService;
import com.testeTecnico.revenda.model.ControleVendasEntity;
import com.testeTecnico.revenda.model.RevendaEntity;
import com.testeTecnico.revenda.model.UsuarioEntity;
import com.testeTecnico.revenda.model.request.AuthRequest;
import com.testeTecnico.revenda.model.request.UsuarioRequestDTO;
import com.testeTecnico.revenda.model.response.AuthResponse;
import com.testeTecnico.revenda.model.response.UsuarioResponseDTO;
import com.testeTecnico.revenda.repository.ControleVendasRepository;
import com.testeTecnico.revenda.repository.RevendaRepository;
import com.testeTecnico.revenda.repository.UsuarioRepository;
import com.testeTecnico.revenda.util.Utils;
import com.testeTecnico.revenda.util.mapper.AuthMapper;
import com.testeTecnico.revenda.util.mapper.UsuarioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

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
    private AuthMapper authMapper;
    @Autowired
    private ControleVendasRepository controleVendasRepository;

    @Autowired
    private Utils utils;


    public ResponseEntity<UsuarioResponseDTO> criarUsuario(UsuarioRequestDTO requestDTO,Authentication auth) {
        try {
            UsuarioEntity usuarioCriador = usuarioRepository.findById(utils.getUsuarioIdFromJwtToken(auth)).get();
            if(usuarioCriador.getRevenda().getId() != requestDTO.revenda_id() && !utils.getUserRole(auth).equals("ROLE_ADMINISTRADOR")){
                return ResponseEntity.badRequest().body(usuarioMapper.toResponseDto("Somente administradores podem criar usuários para outras revendas", null));
            }
            usuarioAtendeRequisitos(requestDTO);
            RevendaEntity revendaEntity = utils.revendaValido(requestDTO.revenda_id());
            UsuarioEntity usuarioEntity = usuarioMapper.toEntityFromRequestDto(requestDTO, revendaEntity);
            usuarioEntity.setSenha(new BCryptPasswordEncoder().encode(requestDTO.senha()));
            usuarioRepository.save(usuarioEntity);
            criaRegistroVendasUsuario(usuarioEntity);
            return ResponseEntity.created(null).body(usuarioMapper.toResponseDto("Usuario cadastrado com sucesso", usuarioEntity));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(usuarioMapper.toResponseDto(e.getMessage(), new UsuarioEntity()));
        }
    }

    public ResponseEntity<UsuarioResponseDTO> modificarInformacoesUsuario(UsuarioRequestDTO requestDTO, Authentication auth){
        try{
            UsuarioEntity usuarioEntity = utils.usuarioValido(requestDTO.id());
            if(utils.getUserRole(auth).equals("ROLE_PROPRIETARIO")){
                Long revendaUsuario = utils.getRevendaIdFromJwtToken(auth);
                if(usuarioEntity.getRevenda().getId() != requestDTO.revenda_id()){
                    return ResponseEntity.badRequest().body(usuarioMapper.toResponseDto("Somente administradores podem " +
                            "mover usuarios entre revendas diferentes", new UsuarioEntity()));
                }
                if(usuarioEntity.getRevenda().getId() == revendaUsuario){
                    return validaRoleFuncionario(usuarioEntity, requestDTO, auth);
                }
                return ResponseEntity.badRequest().body(usuarioMapper.toResponseDto("Este usuario não pertence à sua loja", new UsuarioEntity()));
            }

            return validaRoleFuncionario(usuarioEntity, requestDTO, auth);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(usuarioMapper.toResponseDto(e.getMessage(), new UsuarioEntity()));
        }
    }

    public ResponseEntity<UsuarioResponseDTO> atualizaFuncionario(UsuarioEntity usuarioEntity, UsuarioRequestDTO requestDTO){
        RevendaEntity revenda = revendaRepository.findById(requestDTO.revenda_id()).orElse(null);
        if(Objects.isNull(revenda)){
            return ResponseEntity.badRequest().body(usuarioMapper.toResponseDto("Você esta tentando movendo o usuario para uma revenda inexistente", null));
        }
        usuarioEntity = usuarioMapper.toEntityFromRequestDto(requestDTO, usuarioEntity.getRevenda());
        usuarioEntity.setRevenda(revenda);
        usuarioEntity.setSenha(new BCryptPasswordEncoder().encode(requestDTO.senha()));
        usuarioEntity.setId(requestDTO.id());
        usuarioRepository.save(usuarioEntity);
        return ResponseEntity.ok(usuarioMapper.toResponseDto("Usuario modificado com sucesso", usuarioEntity));
    }

    public ResponseEntity<AuthResponse> login(AuthRequest authRequest){
        try {
            UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.senha());
            Authentication auth = this.authenticationManager.authenticate(credentials);
            UsuarioEntity details = (UsuarioEntity) auth.getPrincipal();
            String cargoUsuario = details.getCargo().toString();
            long id = details.getId();
            String token = tokenService.generateToken((UsuarioEntity) auth.getPrincipal());
            return ResponseEntity.ok(authMapper.toResponseDto("Login efetuado com sucesso", id, cargoUsuario, token));
        }catch (AuthenticationException e){
            return ResponseEntity.ok(authMapper.toResponseDto("Usuario ou senha incorretos", 0L, null,null));

        }
    }

    private void criaRegistroVendasUsuario(UsuarioEntity usuario){
        ControleVendasEntity controleVendasEntity = new ControleVendasEntity();
        controleVendasEntity.setNumero_revendas_atuais(0);
        controleVendasEntity.setUsuario(usuario);
        controleVendasRepository.save(controleVendasEntity);
    }
    private ResponseEntity<UsuarioResponseDTO> validaRoleFuncionario(UsuarioEntity usuarioEntity, UsuarioRequestDTO requestDTO, Authentication auth){
        if(!utils.getUserRole(auth).equals("ROLE_ADMINISTRADOR")){
            return aplicaRegrasParaNaoAdministrador(usuarioEntity, requestDTO);
        }
        return atualizaFuncionario(usuarioEntity, requestDTO);
    }
    private ResponseEntity<UsuarioResponseDTO> aplicaRegrasParaNaoAdministrador(UsuarioEntity usuarioEntity, UsuarioRequestDTO requestDTO){
        if(!usuarioEntity.getCargo().toString().equals("ADMINISTRADOR") && requestDTO.cargo().toString().equals("ADMINISTRADOR")){
            return ResponseEntity.badRequest().body(usuarioMapper.toResponseDto("Você não pode promover o cargo deste usuario para administrador", null));
        } else if(usuarioEntity.getCargo().toString().equals("ADMINISTRADOR") && !requestDTO.cargo().toString().equals("ADMINISTRADOR")){
            return ResponseEntity.badRequest().body(usuarioMapper.toResponseDto("Você não pode rebaixar o cargo deste usuario pois ele é um administrador", null));
        }
        return atualizaFuncionario(usuarioEntity, requestDTO);
    }

    private boolean usuarioAtendeRequisitos(UsuarioRequestDTO requestDTO) throws Exception {
        if(usuarioRepository.findByEmailIgnoreCase(requestDTO.email()).isEmpty()) return true;
        throw new Exception("Email já presente na base");
    }
}
