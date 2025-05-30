package com.testeTecnico.revenda.service;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import com.testeTecnico.revenda.model.RevendaEntity;
import com.testeTecnico.revenda.model.request.RevendaRequestDTO;
import com.testeTecnico.revenda.model.response.RevendaResponseDTO;
import com.testeTecnico.revenda.repository.RevendaRepository;
import com.testeTecnico.revenda.util.mapper.RevendaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RevendaService {

    @Autowired
    private RevendaRepository revendaRepository;

    @Autowired
    private RevendaMapper revendaMapper;

    public ResponseEntity<RevendaResponseDTO> criarRevenda(RevendaRequestDTO requestDTO){
        try {
            revendaAtendeRequisitos(requestDTO);
            RevendaEntity revendaEntity = revendaMapper.toEntityFromRequestDto(requestDTO);
            revendaRepository.save(revendaEntity);
            return ResponseEntity.ok().body(revendaMapper.toResponseDto("Revenda cadastrada com sucesso", revendaEntity));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new RevendaResponseDTO(e.getMessage(), null, null, null));
        }
    }


    public boolean revendaAtendeRequisitos(RevendaRequestDTO requestDTO) throws Exception {
        if(cnpjValido(requestDTO.cnpj()) && !cnpjPresenteNaBase(requestDTO)) return true;
        return false;
    }

    public boolean cnpjValido(String cnpj) throws Exception {
        CNPJValidator cnpjValidator = new CNPJValidator();
        try{
            cnpjValidator.assertValid(cnpj);
            return true;
        }catch (InvalidStateException e){
            throw new Exception("CNPJ INVALIDO");
        }
    }

    public boolean cnpjPresenteNaBase(RevendaRequestDTO requestDTO) throws Exception {
        if(revendaRepository.findByCnpj(requestDTO.cnpj()) != null) throw new Exception("CNPJ PRESENTE NA BASE");
        return false;

    }
}
