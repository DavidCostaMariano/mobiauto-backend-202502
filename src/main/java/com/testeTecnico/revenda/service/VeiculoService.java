package com.testeTecnico.revenda.service;

import com.testeTecnico.revenda.model.RevendaEntity;
import com.testeTecnico.revenda.model.UsuarioEntity;
import com.testeTecnico.revenda.model.VeiculoEntity;
import com.testeTecnico.revenda.model.request.VeiculoRequestDTO;
import com.testeTecnico.revenda.model.response.VeiculoResponseDTO;
import com.testeTecnico.revenda.repository.VeiculoRepository;
import com.testeTecnico.revenda.util.mapper.VeiculoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class VeiculoService {


    @Autowired
    private VeiculoMapper veiculoMapper;

    @Autowired
    private VeiculoRepository veiculoRepository;
    public ResponseEntity<VeiculoResponseDTO> criarVeiculo(VeiculoRequestDTO requestDTO) {
        VeiculoEntity veiculoEntity = veiculoMapper.toEntityFromRequestDto(requestDTO);
        veiculoRepository.save(veiculoEntity);
        return ResponseEntity.ok(
                veiculoMapper.toResponseDto("Veiculo cadastrado com sucesso", veiculoEntity));
    }
}
