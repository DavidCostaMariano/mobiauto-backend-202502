package com.testeTecnico.revenda.service;

import com.testeTecnico.revenda.model.*;
import com.testeTecnico.revenda.model.enums.StatusOportunidade;
import com.testeTecnico.revenda.model.request.OportunidadeRequestDTO;
import com.testeTecnico.revenda.model.request.TransferenciaOportunidadeRequestDTO;
import com.testeTecnico.revenda.model.response.OportunidadeResponseDTO;
import com.testeTecnico.revenda.repository.*;
import com.testeTecnico.revenda.util.Utils;
import com.testeTecnico.revenda.util.ValidacoesResultado;
import com.testeTecnico.revenda.util.mapper.OportunidadeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OportunidadeService {

    @Autowired
    private OportunidadeRepository oportunidadeRepository;

    @Autowired
    private ControleVendasRepository controleVendasRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private RevendaRepository revendaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OportunidadeMapper oportunidadeMapper;

    @Autowired
    private Utils utils;


    public ResponseEntity<List<OportunidadeResponseDTO>> listarOportunidades(long revendaId, Authentication auth){
        Map<String, Object> informacoesJwtToken = utils.recuperarInformacoesJwtToken(auth);
        Long revendaIdDoUsuario = (Long) informacoesJwtToken.get("revenda_id");
        if(utils.getUserRole(auth).equals("ROLE_PROPRIETARIO") && revendaIdDoUsuario != revendaId){
            return ResponseEntity.badRequest().body(Collections.singletonList(oportunidadeMapper.toResponseDto("Você só pode listar oportunidades da sua revenda", null)));
        }
        revendaId = (revendaId == 0 ? revendaIdDoUsuario: revendaId);
        List<OportunidadeEntity> response = oportunidadeRepository.findAllByRevendaIdAndStatusNot(revendaId, StatusOportunidade.CONCLUIDO);
        List<OportunidadeResponseDTO> oportunidades = response.stream()
                .map(item -> oportunidadeMapper.toResponseDto(null, item))
                .collect(Collectors.toList());
        return ResponseEntity.ok(oportunidades);
    }


    public ResponseEntity<OportunidadeResponseDTO> criarOportunidade(OportunidadeRequestDTO requestDTO, Authentication auth){
        try{
            Map<String, Object> informacoesJwtToken = utils.recuperarInformacoesJwtToken(auth);

            Long revendaId = (Long) informacoesJwtToken.get("revenda_id");
            if(utils.getUserRole(auth).equals("ROLE_PROPRIETARIO"))
                return criarOportunidadeProprietario(requestDTO, revendaId);
            return criarRegistroOportunidade(requestDTO, 0);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto(e.getMessage(), null));
        }

    }

    private UsuarioEntity encontrarUsuarioParaOportunidade(long revendaId){
        UsuarioEntity usuarioComMenorNumeroDeVendas = usuarioRepository
                .recuperarUsuarioComMenorNumeroDeVendasAtuais(revendaId);
        return usuarioComMenorNumeroDeVendas;
    }
    private ResponseEntity<OportunidadeResponseDTO> criarOportunidadeProprietario(OportunidadeRequestDTO requestDTO, long revendaId) throws Exception {
        if(revendaId == requestDTO.revendedora_id() || requestDTO.revendedora_id() == 0){
            return criarRegistroOportunidade(requestDTO, revendaId);
        }
        return  ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto("Você só pode criar uma oportunidade para a sua revenda", null));

    }
    private ResponseEntity<OportunidadeResponseDTO> criarRegistroOportunidade(OportunidadeRequestDTO requestDTO, long revendaId) throws Exception {
        ValidacoesResultado validacoes = validarEntidades(requestDTO, revendaId);
        OportunidadeEntity oportunidadeEntity = oportunidadeMapper.toEntityFromRequestDto(
                requestDTO,
                validacoes.cliente(),
                validacoes.veiculo(),
                validacoes.revenda(),
                validacoes.usuario()
        );
        oportunidadeEntity.setStatus(StatusOportunidade.NOVO);
        oportunidadeEntity.setData_atribuicao(LocalDateTime.now());
        oportunidadeRepository.save(oportunidadeEntity);
        adicionaVendaParaUsuario(validacoes.usuario());
        return ResponseEntity.ok(oportunidadeMapper.toResponseDto("Oportunidade cadastrada com sucesso", oportunidadeEntity));
    }

    public ResponseEntity<OportunidadeResponseDTO> alterarStatusOportunidade(OportunidadeRequestDTO requestDTO, Authentication authentication){
        Map<String, Object> informacoesJwtToken = utils.recuperarInformacoesJwtToken(authentication);
        var usuarioId = informacoesJwtToken.get("usuarioId");
        OportunidadeEntity oportunidadeEntity = oportunidadeRepository.findById(requestDTO.id()).orElse(null);
        if(Objects.isNull(oportunidadeEntity)){
            return ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto("Oportunidade não encontrada", null));
        }
        if(oportunidadeEntity.getUsuario().getId() == usuarioId){
            atualizaStatus(oportunidadeEntity, requestDTO);
            return ResponseEntity.ok(oportunidadeMapper.toResponseDto("Status da oportunidade atualizado com sucesso", oportunidadeEntity));
        }
        return ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto("Voce só pode alterar o status de suas oportunidades", null));
    }

    public ResponseEntity<OportunidadeResponseDTO> transferirOportunidade(TransferenciaOportunidadeRequestDTO requestDTO, Authentication authentication) {
        UsuarioEntity usuarioOrigem = null;
        UsuarioEntity usuarioDestino = null;
        if(requestDTO.usuario_origem_id() == requestDTO.usuario_destino_id()) {
            return ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto("Voce está tentando transferir para o mesmo funcionario", null));
        }
        try{
            usuarioOrigem = utils.usuarioValido(requestDTO.usuario_origem_id());
            usuarioDestino = utils.usuarioValido(requestDTO.usuario_destino_id());
            OportunidadeEntity oportunidade = oportunidadeRepository.findById(requestDTO.oportunidade_id())
                    .orElse(null);
            if(!Objects.isNull(oportunidade))
                if(!verificaSeOportunidadePertenceOrigem(oportunidade, usuarioOrigem))
                    return ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto("Essa oportunidade pertence a outro usuario", null));
                if(!verificaSeDestinoOportunidadeMesmaRevenda(usuarioDestino, oportunidade)){
                    return ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto("Você nao pode " +
                            "transferir a oportunidade para um funcionario de outra revenda", null));
                }
                return transfereOportunidade(usuarioDestino, oportunidade);
        }catch (Exception e){
            if(Objects.isNull(usuarioOrigem))
                return ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto("Usuario original nao encontrado", null));
            return ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto("Usuario de destino nao encontrado", null));
        }

    }

    //TODO - TEM QUE TESTAR
    public ResponseEntity<OportunidadeResponseDTO> modificarOportunidade(OportunidadeRequestDTO oportunidadeRequestDTO, Authentication authentication){
        Map<String, Object> informacoesJwtToken = utils.recuperarInformacoesJwtToken(authentication);
        var usuarioId = informacoesJwtToken.get("usuarioId");
        var revendaId = informacoesJwtToken.get("revendaId");
        String roleUsuario = utils.getUserRole(authentication);
        try{
            UsuarioEntity usuario = usuarioRepository.findById(oportunidadeRequestDTO.usuario_id())
                    .orElse(null);
            OportunidadeEntity oportunidade = oportunidadeRepository.findById(oportunidadeRequestDTO.id())
                    .orElse(null);
            if(!Objects.isNull(usuario) && !Objects.isNull(oportunidade)) {
                if (!List.of("ROLE_ADMINISTRADOR", "ROLE_PROPRIETARIO", "ROLE_GERENTE").contains(roleUsuario)) {
                    if (verificaSeOportunidadePertenceOrigem(oportunidade, usuario)) {
                        return atualizaInformacoesOportunidade(oportunidade, oportunidadeRequestDTO);
                    }
                    return ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto("Você nao pode transferir a oportunidade para outro" +
                            "usuario", null));
                }
                return atualizaInformacoesOportunidade(oportunidade, oportunidadeRequestDTO);
            }
            return ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto("Oportunidade ou usuario nao encontrado", null));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto(e.getMessage(), null));
        }
    };

    private ResponseEntity<OportunidadeResponseDTO> atualizaInformacoesOportunidade(OportunidadeEntity oportunidade, OportunidadeRequestDTO requestDTO) throws Exception {
        validarEntidades(requestDTO, oportunidade.getRevenda().getId());
        ValidacoesResultado validacoes = new ValidacoesResultado(
                utils.clienteValido(requestDTO.cliente_id()),
                utils.veiculoValido(requestDTO.veiculo_id()),
                utils.revendaValido(oportunidade.getRevenda().getId()),
                utils.usuarioValido(oportunidade.getUsuario().getId())
        );
        OportunidadeEntity oportunidadeEntity = oportunidadeMapper.toEntityFromRequestDto(
                requestDTO,
                validacoes.cliente(),
                validacoes.veiculo(),
                validacoes.revenda(),
                validacoes.usuario()
        );
        oportunidadeRepository.save(oportunidadeEntity);
        return ResponseEntity.ok(oportunidadeMapper.toResponseDto("Oportunidade atualizada com sucesso", oportunidadeEntity));
    }
    private ResponseEntity<OportunidadeResponseDTO> transfereOportunidade(UsuarioEntity usuarioDestino, OportunidadeEntity oportunidade){
        removeVendaDoUsuario(oportunidade.getUsuario());
        oportunidade.setUsuario(usuarioDestino);
        oportunidade.setData_atribuicao(LocalDateTime.now());
        adicionaVendaParaUsuario(usuarioDestino);
        oportunidadeRepository.save(oportunidade);
        return ResponseEntity.ok().body(oportunidadeMapper.toResponseDto("Oportunidade transferida com sucesso", oportunidade));
    }
    private void atualizaStatus(OportunidadeEntity oportunidadeEntity, OportunidadeRequestDTO requestDTO){
        oportunidadeEntity.setStatus(requestDTO.status());
        if(oportunidadeEntity.getStatus().equals(StatusOportunidade.CONCLUIDO)){
            oportunidadeEntity.setMotivo_conclusao(requestDTO.motivo_conclusao());
            atualizaDataConclusao(oportunidadeEntity);
            removeVendaDoUsuario(oportunidadeEntity.getUsuario());
        }
        oportunidadeRepository.save(oportunidadeEntity);
    }
    private void atualizaDataConclusao(OportunidadeEntity oportunidadeEntity){
        oportunidadeEntity.setData_conclusao(LocalDateTime.now());
    }
    private void removeVendaDoUsuario(UsuarioEntity usuarioEntity){
        ControleVendasEntity controleVendasEntity = controleVendasRepository.findByUsuarioId(usuarioEntity.getId());
        controleVendasEntity.setNumero_revendas_atuais(controleVendasEntity.getNumero_revendas_atuais() - 1);
        controleVendasRepository.save(controleVendasEntity);
    }
    private void adicionaVendaParaUsuario(UsuarioEntity usuario){
        ControleVendasEntity controleVendasEntity = controleVendasRepository.findByUsuarioId(usuario.getId());
        controleVendasEntity.setNumero_revendas_atuais(controleVendasEntity.getNumero_revendas_atuais() + 1);
        controleVendasEntity.setData_ultima_oportunidade(LocalDateTime.now());
        controleVendasRepository.save(controleVendasEntity);
    }

    private ValidacoesResultado validarEntidades(OportunidadeRequestDTO requestDTO, long revendaId) throws Exception {
        revendaId = requestDTO.revendedora_id() == 0 ? revendaId : requestDTO.revendedora_id();
        UsuarioEntity usuario = encontrarUsuarioParaOportunidade(revendaId);
        return new ValidacoesResultado(
                utils.clienteValido(requestDTO.cliente_id()),
                utils.veiculoValido(requestDTO.veiculo_id()),
                utils.revendaValido(revendaId),
                utils.usuarioValido(usuario.getId())
        );
    }

    private boolean verificaSeOportunidadePertenceOrigem(OportunidadeEntity oportunidade, UsuarioEntity usuario){
        if(oportunidade.getUsuario().getId() == usuario.getId())
            return true;
        return false;
    }
    private boolean verificaSeDestinoOportunidadeMesmaRevenda(UsuarioEntity usuarioDestino, OportunidadeEntity oportunidadeEntity){
         if (usuarioDestino.getRevenda() == oportunidadeEntity.getRevenda()) {
            return true;
         }
        return false;
    }





}
