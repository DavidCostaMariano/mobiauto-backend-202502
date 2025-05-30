package com.testeTecnico.revenda;

import com.testeTecnico.revenda.config.security.TokenService;
import com.testeTecnico.revenda.model.*;
import com.testeTecnico.revenda.model.enums.CargosEnum;
import com.testeTecnico.revenda.model.request.AuthRequest;
import com.testeTecnico.revenda.model.request.UsuarioRequestDTO;
import com.testeTecnico.revenda.model.response.AuthResponse;
import com.testeTecnico.revenda.model.response.UsuarioResponseDTO;
import com.testeTecnico.revenda.repository.ControleVendasRepository;
import com.testeTecnico.revenda.repository.RevendaRepository;
import com.testeTecnico.revenda.repository.UsuarioRepository;
import com.testeTecnico.revenda.service.UsuarioService;
import com.testeTecnico.revenda.util.Utils;
import com.testeTecnico.revenda.util.mapper.AuthMapper;
import com.testeTecnico.revenda.util.mapper.UsuarioMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private RevendaRepository revendaRepository;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private TokenService tokenService;
    @Mock private UsuarioMapper usuarioMapper;
    @Mock private AuthMapper authMapper;
    @Mock private ControleVendasRepository controleVendasRepository;
    @Mock private Utils utils;
    @Mock private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCriarUsuarioComSucesso() throws Exception {
        UsuarioRequestDTO dto = new UsuarioRequestDTO(1L, "Joao", "joao@email.com", "123456", CargosEnum.GERENTE, 1L);
        RevendaEntity revenda = new RevendaEntity(); revenda.setId(1L);
        UsuarioEntity criador = new UsuarioEntity(); criador.setId(10L); criador.setRevenda(revenda);
        UsuarioEntity novoUsuario = new UsuarioEntity(); novoUsuario.setEmail(dto.email());

        when(utils.getUsuarioIdFromJwtToken(authentication)).thenReturn(10L);
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(criador));
        when(utils.getUserRole(authentication)).thenReturn("ROLE_PROPRIETARIO");
        when(usuarioRepository.findByEmailIgnoreCase(dto.email())).thenReturn(Optional.empty());
        when(utils.revendaValido(1L)).thenReturn(revenda);
        when(usuarioMapper.toEntityFromRequestDto(dto, revenda)).thenReturn(novoUsuario);

        ResponseEntity<UsuarioResponseDTO> response = usuarioService.criarUsuario(dto, authentication);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        verify(usuarioRepository).save(novoUsuario);
        verify(controleVendasRepository).save(any());
    }

    @Test
    void deveRetornarErroAoCriarUsuarioDeOutraRevendaSemPermissao() throws Exception {
        UsuarioRequestDTO dto = new UsuarioRequestDTO(1L, "Joao", "joao@email.com", "123456", CargosEnum.GERENTE, 99L);
        RevendaEntity revenda = new RevendaEntity(); revenda.setId(1L);
        UsuarioEntity criador = new UsuarioEntity(); criador.setId(10L); criador.setRevenda(revenda);

        when(utils.getUsuarioIdFromJwtToken(authentication)).thenReturn(10L);
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(criador));
        when(utils.getUserRole(authentication)).thenReturn("ROLE_PROPRIETARIO");

        ResponseEntity<UsuarioResponseDTO> response = usuarioService.criarUsuario(dto, authentication);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        verify(usuarioMapper).toResponseDto(contains("outras revendas"), isNull());
    }

    @Test
    void deveEfetuarLoginComSucesso() {
        AuthRequest request = new AuthRequest("email@test.com", "123456");
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(1L);
        usuario.setCargo(CargosEnum.GERENTE);

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(usuario);
        when(tokenService.generateToken(usuario)).thenReturn("fake-jwt");

        ResponseEntity<AuthResponse> response = usuarioService.login(request);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(authenticationManager).authenticate(any());
        verify(authMapper).toResponseDto(any(), anyLong(), any(), any());
    }


    @Test
    void deveRetornarMensagemErroLoginInvalido() {
        AuthRequest request = new AuthRequest("email@test.com", "123456");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));
        ResponseEntity<AuthResponse> response = usuarioService.login(request);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(authMapper).toResponseDto(contains("incorretos"), eq(0L), isNull(), isNull());
    }


    @Test
    void deveModificarInformacoesUsuarioQuandoProprietarioEDaMesmaRevenda() throws Exception {
        UsuarioRequestDTO dto = new UsuarioRequestDTO(1L, "Maria", "maria@email.com", "123456", CargosEnum.GERENTE, 1L);

        RevendaEntity revenda = new RevendaEntity(); revenda.setId(1L);
        UsuarioEntity usuario = new UsuarioEntity(); usuario.setId(1L); usuario.setRevenda(revenda);
        usuario.setCargo(CargosEnum.PROPRIETARIO);
        when(utils.usuarioValido(1L)).thenReturn(usuario);
        when(utils.getUserRole(authentication)).thenReturn("ROLE_PROPRIETARIO");
        when(utils.getRevendaIdFromJwtToken(authentication)).thenReturn(1L);
        when(revendaRepository.findById(anyLong())).thenReturn(Optional.of(revenda));
        when(usuarioMapper.toEntityFromRequestDto(any(), any())).thenReturn(usuario);
        ResponseEntity<UsuarioResponseDTO> response = usuarioService.modificarInformacoesUsuario(dto, authentication);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void deveRetornarErroQuandoProprietarioTentaMoverUsuarioEntreRevendas() throws Exception {
        UsuarioRequestDTO dto = new UsuarioRequestDTO(1L, "Maria", "maria@email.com", "123456", CargosEnum.GERENTE, 99L);

        RevendaEntity revenda = new RevendaEntity(); revenda.setId(1L);
        UsuarioEntity usuario = new UsuarioEntity(); usuario.setId(1L); usuario.setRevenda(revenda);

        when(utils.usuarioValido(1L)).thenReturn(usuario);
        when(utils.getUserRole(authentication)).thenReturn("ROLE_PROPRIETARIO");

        ResponseEntity<UsuarioResponseDTO> response = usuarioService.modificarInformacoesUsuario(dto, authentication);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        verify(usuarioMapper).toResponseDto(contains("mover usuarios entre revendas"), any());
    }

    @Test
    void deveRetornarErroQuandoUsuarioNaoPertenceALojaDoProprietario() throws Exception {
        UsuarioRequestDTO dto = new UsuarioRequestDTO(1L, "Maria", "maria@email.com", "123456", CargosEnum.GERENTE, 1L);

        RevendaEntity revUsuario = new RevendaEntity(); revUsuario.setId(2L);
        UsuarioEntity usuario = new UsuarioEntity(); usuario.setId(1L); usuario.setRevenda(revUsuario);

        when(utils.usuarioValido(1L)).thenReturn(usuario);
        when(utils.getUserRole(authentication)).thenReturn("ROLE_PROPRIETARIO");
        when(utils.getRevendaIdFromJwtToken(authentication)).thenReturn(1L);

        ResponseEntity<UsuarioResponseDTO> response = usuarioService.modificarInformacoesUsuario(dto, authentication);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
    }


}