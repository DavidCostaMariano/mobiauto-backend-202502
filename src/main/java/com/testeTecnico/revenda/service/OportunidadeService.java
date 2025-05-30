package com.testeTecnico.revenda.service;

import com.testeTecnico.revenda.model.*;
import com.testeTecnico.revenda.model.enums.StatusOportunidade;
import com.testeTecnico.revenda.model.request.ModificaOportunidadeRequestDTO;
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
        Long revendaIdDoUsuario = utils.getRevendaIdFromJwtToken(auth);
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
            Long revendaId = requestDTO.revendedora_id();
            Long usuarioProprietarioId = utils.getUsuarioIdFromJwtToken(auth);
            if(requestDTO.revendedora_id() == 0)
                revendaId = utils.getRevendaIdFromJwtToken(auth);
            if(utils.getUserRole(auth).equals("ROLE_PROPRIETARIO"))
                return criarOportunidadeProprietario(requestDTO, revendaId, usuarioProprietarioId);
            return criarRegistroOportunidade(requestDTO, revendaId);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto(e.getMessage(), null));
        }

    }


    public ResponseEntity<OportunidadeResponseDTO> alterarStatusOportunidade(OportunidadeRequestDTO requestDTO, Authentication auth){
        var usuarioId = utils.getUsuarioIdFromJwtToken(auth);
        OportunidadeEntity oportunidadeEntity = oportunidadeRepository.findById(requestDTO.id()).orElse(null);
        if(Objects.isNull(oportunidadeEntity)){
            return ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto("Oportunidade não encontrada", null));
        }
        if(oportunidadeEntity.getUsuario().getId() == usuarioId){
            atualizaStatusOportunidade(oportunidadeEntity, requestDTO);
            return ResponseEntity.ok(oportunidadeMapper.toResponseDto("Status da oportunidade atualizado com sucesso", oportunidadeEntity));
        }
        return ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto("Voce só pode alterar o status de suas oportunidades. Esta oportunidade " +
                "nao pertence a você.", null));
    }

    public ResponseEntity<OportunidadeResponseDTO> transferirOportunidade(TransferenciaOportunidadeRequestDTO requestDTO, Authentication authentication) {
        UsuarioEntity usuarioOrigem = null;
        UsuarioEntity usuarioDestino;
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

    public ResponseEntity<OportunidadeResponseDTO> modificarOportunidade(ModificaOportunidadeRequestDTO oportunidadeRequestDTO, Authentication auth){
        String roleUsuario = utils.getUserRole(auth);
        Long usuarioId = utils.getUsuarioIdFromJwtToken(auth);
        try{
            OportunidadeEntity oportunidade = oportunidadeRepository.findById(oportunidadeRequestDTO.oportunidade_id())
                    .orElse(null);
            UsuarioEntity usuario = usuarioRepository.findById(oportunidadeRequestDTO.usuario_id())
                    .orElse(oportunidade.getUsuario());

            if(!Objects.isNull(oportunidade)) {
                if (!verificaSeOportunidadePertenceOrigem(oportunidade, usuario)) {
                    return ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto("Você nao pode transferir a oportunidade para outro " +
                            "usuario por aqui utilize o endpoint tranferir-oportunidade", null));
                }
                if(roleUsuario.equals("ROLE_ASSISTENTE")){
                    if(oportunidade.getUsuario().getId() ==usuarioId){
                        return atualizaInformacoesOportunidade(oportunidade, oportunidadeRequestDTO);
                    }
                    return ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto("Você nao pode modificar uma " +
                            "oportunidade que nao e sua", null));

                }
                return atualizaInformacoesOportunidade(oportunidade, oportunidadeRequestDTO);
            }
            return ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto("Oportunidade ou usuario nao encontrado", null));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto(e.getMessage(), null));
        }
    };

    private ResponseEntity<OportunidadeResponseDTO> atualizaInformacoesOportunidade(OportunidadeEntity oportunidade, ModificaOportunidadeRequestDTO requestDTO) throws Exception {
        OportunidadeRequestDTO oportunidadeRequestDTO = oportunidadeMapper.toOportunidadeRequestFromModificaOportunidadeRequest(requestDTO);
        ValidacoesResultado validacoes = validarEntidades(oportunidadeRequestDTO, oportunidade.getRevenda().getId());
        if(verificaSeVeiculoEstaAtreladoAOutraOportunidade(validacoes.veiculo(), oportunidadeRequestDTO)){
            return ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto("Este veiculo ja esta em negociacao com outro cliente", null));
        };
        OportunidadeEntity oportunidadeEntity = oportunidadeMapper.toEntityFromRequestDto(
                oportunidadeRequestDTO,
                validacoes.cliente(),
                validacoes.veiculo(),
                validacoes.revenda(),
                oportunidade.getUsuario()
        );
        if(oportunidade.getStatus().toString().equals("CONCLUIDO")){
            oportunidadeEntity.setData_conclusao(oportunidade.getData_conclusao());
            oportunidadeEntity.setMotivo_conclusao(oportunidade.getMotivo_conclusao());
        }
        oportunidadeEntity.setData_atribuicao(oportunidade.getData_atribuicao());
        oportunidadeEntity.setStatus(oportunidade.getStatus());
        oportunidadeEntity.setId(oportunidade.getId());
        oportunidadeRepository.save(oportunidadeEntity);
        return ResponseEntity.ok(oportunidadeMapper.toResponseDto("Oportunidade atualizada com sucesso", oportunidadeEntity));
    }

    private ResponseEntity<OportunidadeResponseDTO> transfereOportunidade(UsuarioEntity usuarioDestino, OportunidadeEntity oportunidade){
        if(!oportunidade.getStatus().toString().equals("CONCLUIDO")){
            removeVendaDoUsuario(oportunidade.getUsuario());
            oportunidade.setUsuario(usuarioDestino);
            adicionaVendaParaUsuario(usuarioDestino);
        }
        oportunidade.setUsuario(usuarioDestino);
        oportunidade.setData_atribuicao(LocalDateTime.now());
        oportunidadeRepository.save(oportunidade);

        return ResponseEntity.ok().body(oportunidadeMapper.toResponseDto("Oportunidade transferida com sucesso", oportunidade));
    }
    private void atualizaStatusOportunidade(OportunidadeEntity oportunidadeEntity, OportunidadeRequestDTO requestDTO){

        if(!verificaSeStatusOportunidadeOuRequestConcluido(oportunidadeEntity, requestDTO)){
            oportunidadeEntity.setStatus(requestDTO.status());
            oportunidadeRepository.save(oportunidadeEntity);
        }


    }
    private UsuarioEntity encontrarUsuarioParaOportunidade(long revendaId){
        UsuarioEntity usuarioComMenorNumeroDeVendas = usuarioRepository
                .recuperarUsuarioComMenorNumeroDeVendasAtuais(revendaId);
        return usuarioComMenorNumeroDeVendas;
    }
    private ResponseEntity<OportunidadeResponseDTO> criarOportunidadeProprietario(OportunidadeRequestDTO requestDTO, long revendaId, long usuarioProprietarioId) throws Exception {
        UsuarioEntity usuarioProprietario = usuarioRepository.findById(usuarioProprietarioId)
                .orElse(null);
        if(!Objects.isNull(usuarioProprietario) && usuarioProprietario.getRevenda().getId() == revendaId){
            return criarRegistroOportunidade(requestDTO, revendaId);
        }
        return  ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto("Você só pode criar uma oportunidade para a sua revenda", null));

    }
    private ResponseEntity<OportunidadeResponseDTO> criarRegistroOportunidade(OportunidadeRequestDTO requestDTO, long revendaId) throws Exception {
        ValidacoesResultado validacoes = validarEntidades(requestDTO, revendaId);
        if(verificaSeVeiculoEstaAtreladoAOutraOportunidade(validacoes.veiculo(), requestDTO)){
            return ResponseEntity.badRequest().body(oportunidadeMapper.toResponseDto("Este veiculo ja esta em negociacao com outro cliente", null));
        };
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

    private boolean verificaSeVeiculoEstaAtreladoAOutraOportunidade(VeiculoEntity veiculo, OportunidadeRequestDTO requestDTO){
        OportunidadeEntity oportunidade = oportunidadeRepository.findByVeiculoId(veiculo.getId());
        if(Objects.isNull(oportunidade)){
            return false;
        }
        if(oportunidade.getRevenda().getId() == requestDTO.revendedora_id()){
            return false;
        }
        return true;
    }
    private boolean verificaSeStatusOportunidadeOuRequestConcluido(OportunidadeEntity oportunidadeEntity, OportunidadeRequestDTO requestDTO){
        if(requestDTO.status().toString().equals("CONCLUIDO")){
            if(!oportunidadeEntity.getStatus().toString().equals("CONCLUIDO"))
                removeVendaDoUsuario(oportunidadeEntity.getUsuario());
            oportunidadeEntity.setStatus(requestDTO.status());
            String motivo_conclusao = (Objects.isNull(requestDTO.motivo_conclusao())? "Motivo nao informado": requestDTO.motivo_conclusao());
            oportunidadeEntity.setMotivo_conclusao(motivo_conclusao);
            oportunidadeEntity.setData_conclusao(LocalDateTime.now());
            oportunidadeRepository.save(oportunidadeEntity);
            return true;
        } else if(oportunidadeEntity.getStatus().toString().equals("CONCLUIDO")){
            oportunidadeEntity.setStatus(requestDTO.status());
            oportunidadeEntity.setMotivo_conclusao(null);
            oportunidadeEntity.setData_conclusao(null);
            adicionaVendaParaUsuario(oportunidadeEntity.getUsuario());
            oportunidadeRepository.save(oportunidadeEntity);
            return true;
        }
        return false;
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
