package com.testeTecnico.revenda;

import com.testeTecnico.revenda.model.*;
import com.testeTecnico.revenda.model.enums.StatusOportunidade;
import com.testeTecnico.revenda.model.request.ModificaOportunidadeRequestDTO;
import com.testeTecnico.revenda.model.request.OportunidadeRequestDTO;
import com.testeTecnico.revenda.model.request.TransferenciaOportunidadeRequestDTO;
import com.testeTecnico.revenda.model.response.OportunidadeResponseDTO;
import com.testeTecnico.revenda.repository.*;
import com.testeTecnico.revenda.service.OportunidadeService;
import com.testeTecnico.revenda.util.Utils;
import com.testeTecnico.revenda.util.mapper.OportunidadeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OportunidadeServiceTest {

    @InjectMocks
    private OportunidadeService oportunidadeService;
    @Captor
    private ArgumentCaptor<OportunidadeEntity> opportunityCaptor;

    @Mock private OportunidadeRepository oportunidadeRepository;
    @Mock private ControleVendasRepository controleVendasRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private VeiculoRepository veiculoRepository;
    @Mock private RevendaRepository revendaRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private OportunidadeMapper oportunidadeMapper;
    @Mock private Utils utils;

    @Mock private Authentication authentication;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void deveListarOportunidadesDaRevendaQuandoUsuarioForPermitido() {
        Long revendaId = 1L;

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(1L);

        OportunidadeEntity oportunidade = new OportunidadeEntity();
        oportunidade.setId(10L);

        OportunidadeResponseDTO responseDTO = oportunidadeMapper.toResponseDto("teste", oportunidade);

        when(utils.getRevendaIdFromJwtToken(authentication)).thenReturn(1L);
        when(utils.getUserRole(authentication)).thenReturn("ROLE_GERENTE");

        when(oportunidadeRepository.findAllByRevendaIdAndStatusNot(1L, StatusOportunidade.CONCLUIDO))
                .thenReturn(List.of(oportunidade));
        when(oportunidadeMapper.toResponseDto(null, oportunidade)).thenReturn(responseDTO);

        ResponseEntity<List<OportunidadeResponseDTO>> response = oportunidadeService.listarOportunidades(revendaId, authentication);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).contains(responseDTO);
    }

    @Test
    void naoDeveListarOportunidadesDaRevendaQuandoUsuarioNaoForPermitido() {
        Long revendaId = 2L;

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(1L);
        usuario.setRevenda(new RevendaEntity());
        usuario.getRevenda().setId(2L);
        OportunidadeEntity oportunidade = new OportunidadeEntity();
        oportunidade.setId(10L);

        OportunidadeResponseDTO responseDTO = oportunidadeMapper.toResponseDto("teste", oportunidade);

        when(utils.getRevendaIdFromJwtToken(authentication)).thenReturn(1L);
        when(utils.getUserRole(authentication)).thenReturn("ROLE_PROPRIETARIO");

        when(oportunidadeRepository.findAllByRevendaIdAndStatusNot(2L, StatusOportunidade.CONCLUIDO))
                .thenReturn(List.of(oportunidade));
        when(oportunidadeMapper.toResponseDto(null, oportunidade)).thenReturn(responseDTO);

        ResponseEntity<List<OportunidadeResponseDTO>> response = oportunidadeService.listarOportunidades(revendaId, authentication);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    void deveRetornarErroAoCriarOportunidadeSeVeiculoEstiverEmNegociacao() throws Exception {
        OportunidadeRequestDTO dto = new OportunidadeRequestDTO(
                1L, StatusOportunidade.NOVO, "", null, null, 1, 1, 1,1
        );
        ClienteEntity cliente = new ClienteEntity();
        VeiculoEntity veiculo = new VeiculoEntity();
        RevendaEntity revenda = new RevendaEntity();
        revenda.setId(99l);
        UsuarioEntity usuario = new UsuarioEntity();
        veiculo.setId(1L);
        usuario.setId(1l);

        OportunidadeEntity existente = new OportunidadeEntity();
        existente.setVeiculo(veiculo);
        existente.setId(99L);
        existente.setRevenda(revenda);
        when(utils.getUserRole(authentication)).thenReturn("ROLE_GERENTE");
        when(utils.getRevendaIdFromJwtToken(authentication)).thenReturn(1L);
        when(usuarioRepository.recuperarUsuarioComMenorNumeroDeVendasAtuais(1L)).thenReturn(usuario);
        when(utils.clienteValido(1L)).thenReturn(cliente);
        when(utils.veiculoValido(1L)).thenReturn(veiculo);
        when(utils.revendaValido(1L)).thenReturn(revenda);
        when(utils.usuarioValido(anyLong())).thenReturn(usuario);
        when(oportunidadeRepository.findByVeiculoId(1L)).thenReturn(existente);

        ResponseEntity<OportunidadeResponseDTO> response = oportunidadeService.criarOportunidade(dto, authentication);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    void deveCriarOportunidadeParaProprietario() throws Exception {
        OportunidadeRequestDTO dto = new OportunidadeRequestDTO(
                1L, StatusOportunidade.NOVO, "", null, null, 1, 1, 1,1
        );
        ControleVendasEntity controleVendas = new ControleVendasEntity();
        controleVendas.setNumero_revendas_atuais(1);

        ClienteEntity cliente = new ClienteEntity();
        VeiculoEntity veiculo = new VeiculoEntity();
        RevendaEntity revenda = new RevendaEntity();
        revenda.setId(1l);
        UsuarioEntity usuario = new UsuarioEntity();
        veiculo.setId(1L);
        usuario.setId(1l);
        OportunidadeEntity existente = new OportunidadeEntity();
        existente.setVeiculo(veiculo);
        existente.setId(1L);
        existente.setRevenda(revenda);
        usuario.setRevenda(revenda);
        when(utils.getUserRole(authentication)).thenReturn("ROLE_PROPRIETARIO");
        when(utils.getRevendaIdFromJwtToken(authentication)).thenReturn(1L);
        when(usuarioRepository.recuperarUsuarioComMenorNumeroDeVendasAtuais(1L)).thenReturn(usuario);
        when(utils.clienteValido(1L)).thenReturn(cliente);
        when(utils.veiculoValido(1L)).thenReturn(veiculo);
        when(utils.revendaValido(1L)).thenReturn(revenda);
        when(utils.usuarioValido(anyLong())).thenReturn(usuario);
        when(oportunidadeRepository.findByVeiculoId(1L)).thenReturn(existente);
        when(usuarioRepository.findById(any())).thenReturn(Optional.of(usuario));
        when(oportunidadeMapper.toEntityFromRequestDto(any(), any(), any(),any(), any())).thenReturn(new OportunidadeEntity());
        when(controleVendasRepository.findByUsuarioId(1L)).thenReturn(controleVendas);
        ResponseEntity<OportunidadeResponseDTO> response = oportunidadeService.criarOportunidade(dto, authentication);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void deveAlterarStatusOportunidade(){
        ControleVendasEntity controleVendasEntity = new ControleVendasEntity();
        controleVendasEntity.setNumero_revendas_atuais(1);
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(1l);
        OportunidadeEntity oportunidade = new OportunidadeEntity();
        oportunidade.setId(1L);
        oportunidade.setStatus(StatusOportunidade.NOVO);
        oportunidade.setUsuario(usuario);
        when(oportunidadeRepository.findById(anyLong())).thenReturn(Optional.of(oportunidade));
        when(controleVendasRepository.findByUsuarioId(anyLong())).thenReturn(controleVendasEntity);
        when(utils.getUserRole(authentication)).thenReturn("ROLE_PROPRIETARIO");
        when(utils.getUsuarioIdFromJwtToken(authentication)).thenReturn(1l);
        OportunidadeRequestDTO dto = new OportunidadeRequestDTO(
                1L, StatusOportunidade.EM_ATENDIMENTO, "", null, null, 1, 1, 1,1
        );
        ResponseEntity<OportunidadeResponseDTO> response = oportunidadeService.alterarStatusOportunidade(dto, authentication);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void deveAlterarStatusOportunidadeParaConcluido(){
        ControleVendasEntity controleVendasEntity = new ControleVendasEntity();
        controleVendasEntity.setNumero_revendas_atuais(1);
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(1l);
        OportunidadeEntity oportunidade = new OportunidadeEntity();
        oportunidade.setId(1L);
        oportunidade.setStatus(StatusOportunidade.NOVO);
        oportunidade.setUsuario(usuario);
        when(oportunidadeRepository.findById(anyLong())).thenReturn(Optional.of(oportunidade));
        when(controleVendasRepository.findByUsuarioId(anyLong())).thenReturn(controleVendasEntity);
        when(utils.getUserRole(authentication)).thenReturn("ROLE_PROPRIETARIO");
        when(utils.getUsuarioIdFromJwtToken(authentication)).thenReturn(1l);
        OportunidadeRequestDTO dto = new OportunidadeRequestDTO(
                1L, StatusOportunidade.CONCLUIDO, "", null, null, 1, 1, 1,1
        );
        ResponseEntity<OportunidadeResponseDTO> response = oportunidadeService.alterarStatusOportunidade(dto, authentication);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void deveAlterarStatusDeOportunidadeConcluidaParaOutroStatus(){
        ControleVendasEntity controleVendasEntity = new ControleVendasEntity();
        controleVendasEntity.setNumero_revendas_atuais(1);
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(1l);
        OportunidadeEntity oportunidade = new OportunidadeEntity();
        oportunidade.setId(1L);
        oportunidade.setStatus(StatusOportunidade.CONCLUIDO);
        oportunidade.setUsuario(usuario);
        when(oportunidadeRepository.findById(anyLong())).thenReturn(Optional.of(oportunidade));
        when(controleVendasRepository.findByUsuarioId(anyLong())).thenReturn(controleVendasEntity);
        when(utils.getUserRole(authentication)).thenReturn("ROLE_PROPRIETARIO");
        when(utils.getUsuarioIdFromJwtToken(authentication)).thenReturn(1l);
        OportunidadeRequestDTO dto = new OportunidadeRequestDTO(
                1L, StatusOportunidade.EM_ATENDIMENTO, "", null, null, 1, 1, 1,1
        );
        ResponseEntity<OportunidadeResponseDTO> response = oportunidadeService.alterarStatusOportunidade(dto, authentication);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void naoDeveAlterarStatusOportunidadeNaoEncontrada(){

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(1l);
        OportunidadeEntity oportunidade = new OportunidadeEntity();
        oportunidade.setId(1L);
        oportunidade.setStatus(StatusOportunidade.NOVO);
        oportunidade.setUsuario(usuario);
        when(utils.getUserRole(authentication)).thenReturn("ROLE_PROPRIETARIO");
        when(utils.getUsuarioIdFromJwtToken(authentication)).thenReturn(1l);
        OportunidadeRequestDTO dto = new OportunidadeRequestDTO(
                1L, StatusOportunidade.CONCLUIDO, "", null, null, 1, 1, 1,1
        );
        ResponseEntity<OportunidadeResponseDTO> response = oportunidadeService.alterarStatusOportunidade(dto, authentication);
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    void deveRetornarErroQuandoUsuarioNaoEhDonoDaOportunidade() {
        ModificaOportunidadeRequestDTO dto = mock(ModificaOportunidadeRequestDTO.class);
        when(dto.oportunidade_id()).thenReturn(10L);
        when(dto.usuario_id()).thenReturn(2L);

        OportunidadeEntity oportunidade = new OportunidadeEntity();
        UsuarioEntity donoAtual = new UsuarioEntity(); donoAtual.setId(1L);
        oportunidade.setUsuario(donoAtual);

        UsuarioEntity usuarioRequest = new UsuarioEntity(); usuarioRequest.setId(2L);

        when(utils.getUserRole(authentication)).thenReturn("ROLE_ASSISTENTE");
        when(utils.getUsuarioIdFromJwtToken(authentication)).thenReturn(1L);
        when(oportunidadeRepository.findById(10L)).thenReturn(Optional.of(oportunidade));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioRequest));

        ResponseEntity<OportunidadeResponseDTO> response = oportunidadeService.modificarOportunidade(dto, authentication);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        verify(oportunidadeMapper).toResponseDto(contains("utilize o endpoint tranferir-oportunidade"), isNull());
    }

    @Test
    void deveAtualizarQuandoAssistenteForODonoDaOportunidade() throws Exception {
        ModificaOportunidadeRequestDTO dto = mock(ModificaOportunidadeRequestDTO.class);
        when(dto.oportunidade_id()).thenReturn(10L);
        when(dto.usuario_id()).thenReturn(1L);

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(1L);
        RevendaEntity revenda = new RevendaEntity();
        revenda.setId(1L);
        VeiculoEntity veiculo = new VeiculoEntity();
        veiculo.setId(1L);
        ClienteEntity cliente = new ClienteEntity();

        OportunidadeEntity oportunidade = new OportunidadeEntity();
        oportunidade.setId(10L);
        oportunidade.setUsuario(usuario);
        oportunidade.setRevenda(revenda);
        oportunidade.setStatus(StatusOportunidade.NOVO);
        when(utils.getUserRole(authentication)).thenReturn("ROLE_ASSISTENTE");
        when(utils.getUsuarioIdFromJwtToken(authentication)).thenReturn(1L);
        when(oportunidadeRepository.findById(10L)).thenReturn(Optional.of(oportunidade));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.recuperarUsuarioComMenorNumeroDeVendasAtuais(anyLong())).thenReturn(usuario);

        when(utils.clienteValido(anyLong())).thenReturn(cliente);
        when(utils.veiculoValido(anyLong())).thenReturn(veiculo);
        when(utils.revendaValido(anyLong())).thenReturn(revenda);
        when(utils.usuarioValido(anyLong())).thenReturn(usuario);
        when(oportunidadeMapper.toOportunidadeRequestFromModificaOportunidadeRequest(dto)).thenReturn(
                new OportunidadeRequestDTO(1L, StatusOportunidade.NOVO, null, null, null, 1l, 1l, 1l, 1l)
        );
        when(oportunidadeMapper.toEntityFromRequestDto(any(), any(), any(), any(), any())).thenReturn(oportunidade);

        ResponseEntity<OportunidadeResponseDTO> response = oportunidadeService.modificarOportunidade(dto, authentication);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void deveRetornarErroQuandoOportunidadeNaoForEncontrada() {
        ModificaOportunidadeRequestDTO dto = mock(ModificaOportunidadeRequestDTO.class);
        when(dto.oportunidade_id()).thenReturn(10L);
        when(utils.getUserRole(authentication)).thenReturn("ROLE_GERENTE");
        when(utils.getUsuarioIdFromJwtToken(authentication)).thenReturn(1L);
        when(oportunidadeRepository.findById(10L)).thenReturn(Optional.empty());

        ResponseEntity<OportunidadeResponseDTO> response = oportunidadeService.modificarOportunidade(dto, authentication);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    void deveRetornarErroTratadoEmCasoDeException() {
        ModificaOportunidadeRequestDTO dto = mock(ModificaOportunidadeRequestDTO.class);
        when(dto.oportunidade_id()).thenReturn(10L);
        when(dto.usuario_id()).thenReturn(1L);
        when(utils.getUserRole(authentication)).thenReturn("ROLE_GERENTE");
        when(utils.getUsuarioIdFromJwtToken(authentication)).thenReturn(1L);
        when(oportunidadeRepository.findById(10L)).thenThrow(new RuntimeException("Erro inesperado"));

        ResponseEntity<OportunidadeResponseDTO> response = oportunidadeService.modificarOportunidade(dto, authentication);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        verify(oportunidadeMapper).toResponseDto(contains("Erro inesperado"), isNull());
    }


    @Test
    void deveRetornarErroQuandoOrigemIgualDestino() {
        TransferenciaOportunidadeRequestDTO dto = new TransferenciaOportunidadeRequestDTO(1L, 1L, 10L);


        ResponseEntity<OportunidadeResponseDTO> response = oportunidadeService.transferirOportunidade(dto, authentication);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        verify(oportunidadeMapper).toResponseDto(contains("mesmo funcionario"), isNull());
    }

    @Test
    void deveRetornarErroQuandoUsuarioOrigemNaoExiste() throws Exception {
        TransferenciaOportunidadeRequestDTO dto = new TransferenciaOportunidadeRequestDTO(1L, 2L, 10L);

        when(utils.usuarioValido(1L)).thenThrow(new RuntimeException("Origem não encontrado"));

        ResponseEntity<OportunidadeResponseDTO> response = oportunidadeService.transferirOportunidade(dto, authentication);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        verify(oportunidadeMapper).toResponseDto(contains("Usuario original"), isNull());
    }

    @Test
    void deveRetornarErroQuandoUsuarioDestinoNaoExiste() throws Exception {
        TransferenciaOportunidadeRequestDTO dto = new TransferenciaOportunidadeRequestDTO(1L, 2L, 10L);

        UsuarioEntity origem = new UsuarioEntity();
        origem.setId(1L);

        when(utils.usuarioValido(1L)).thenReturn(origem);
        when(utils.usuarioValido(2L)).thenThrow(new RuntimeException("Destino não encontrado"));

        ResponseEntity<OportunidadeResponseDTO> response = oportunidadeService.transferirOportunidade(dto, authentication);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        verify(oportunidadeMapper).toResponseDto(contains("Usuario de destino"), isNull());
    }

    @Test
    void deveRetornarErroSeOportunidadeNaoPertencerAoUsuarioOrigem() throws Exception {
        TransferenciaOportunidadeRequestDTO dto = new TransferenciaOportunidadeRequestDTO(1L, 2L, 10L);

        UsuarioEntity origem = new UsuarioEntity(); origem.setId(1L);
        UsuarioEntity donoReal = new UsuarioEntity(); donoReal.setId(999L);
        UsuarioEntity destino = new UsuarioEntity(); destino.setId(2L);

        OportunidadeEntity oportunidade = new OportunidadeEntity();
        oportunidade.setUsuario(donoReal);

        when(utils.usuarioValido(1L)).thenReturn(origem);
        when(utils.usuarioValido(2L)).thenReturn(destino);
        when(oportunidadeRepository.findById(10L)).thenReturn(Optional.of(oportunidade));

        ResponseEntity<OportunidadeResponseDTO> response = oportunidadeService.transferirOportunidade(dto, authentication);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        verify(oportunidadeMapper).toResponseDto(contains("pertence a outro usuario"), isNull());
    }

    @Test
    void deveRetornarErroQuandoRevendasForemDiferentes() throws Exception {
        TransferenciaOportunidadeRequestDTO dto = new TransferenciaOportunidadeRequestDTO(1L, 2L, 10L);

        UsuarioEntity origem = new UsuarioEntity(); origem.setId(1L);
        UsuarioEntity destino = new UsuarioEntity(); destino.setId(2L);
        UsuarioEntity dono = new UsuarioEntity(); dono.setId(1L);

        RevendaEntity revOrigem = new RevendaEntity(); revOrigem.setId(1L);
        RevendaEntity revDestino = new RevendaEntity(); revDestino.setId(2L);

        dono.setRevenda(revOrigem);
        destino.setRevenda(revDestino);

        OportunidadeEntity oportunidade = new OportunidadeEntity();
        oportunidade.setUsuario(dono);
        oportunidade.setRevenda(revOrigem);

        when(utils.usuarioValido(1L)).thenReturn(origem);
        when(utils.usuarioValido(2L)).thenReturn(destino);
        when(oportunidadeRepository.findById(10L)).thenReturn(Optional.of(oportunidade));

        ResponseEntity<OportunidadeResponseDTO> response = oportunidadeService.transferirOportunidade(dto, authentication);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        verify(oportunidadeMapper).toResponseDto(contains("outra revenda"), isNull());
    }

    @Test
    void deveTransferirOportunidadeComSucesso() throws Exception {
        TransferenciaOportunidadeRequestDTO dto = new TransferenciaOportunidadeRequestDTO(1L, 2L, 10L);

        ControleVendasEntity controleVendas = new ControleVendasEntity();
        controleVendas.setNumero_revendas_atuais(1);
        UsuarioEntity origem = new UsuarioEntity();
        origem.setId(1L);
        UsuarioEntity destino = new UsuarioEntity();
        destino.setId(2L);
        RevendaEntity revenda = new RevendaEntity();
        revenda.setId(1L);

        origem.setRevenda(revenda);
        destino.setRevenda(revenda);

        OportunidadeEntity oportunidade = new OportunidadeEntity();
        oportunidade.setUsuario(origem);
        oportunidade.setRevenda(revenda);
        oportunidade.setStatus(StatusOportunidade.NOVO);

        when(utils.usuarioValido(1L)).thenReturn(origem);
        when(utils.usuarioValido(2L)).thenReturn(destino);
        when(oportunidadeRepository.findById(10L)).thenReturn(Optional.of(oportunidade));
        when(controleVendasRepository.findByUsuarioId(anyLong())).thenReturn(controleVendas);
        when(usuarioRepository.findById(origem.getId())).thenReturn(Optional.of(origem));
        when(usuarioRepository.findById(destino.getId())).thenReturn(Optional.of(destino));
        ResponseEntity<OportunidadeResponseDTO> response = oportunidadeService.transferirOportunidade(dto, authentication);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(oportunidadeRepository).save(opportunityCaptor.capture());
        assertThat(opportunityCaptor.getValue().getUsuario().getId()).isEqualTo(2L);
    }
}
