package com.testeTecnico.revenda;

import com.testeTecnico.revenda.model.VeiculoEntity;
import com.testeTecnico.revenda.model.request.VeiculoRequestDTO;
import com.testeTecnico.revenda.model.response.VeiculoResponseDTO;
import com.testeTecnico.revenda.repository.VeiculoRepository;
import com.testeTecnico.revenda.service.VeiculoService;
import com.testeTecnico.revenda.util.mapper.VeiculoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {


    @InjectMocks
    private VeiculoService veiculoService;

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private VeiculoMapper veiculoMapper;

    @Test
    void deveCriarVeiculoComSucesso() {
        VeiculoRequestDTO dto = new VeiculoRequestDTO(0l,"Fiat", "Uno","Mille", "2012");
        VeiculoEntity entity = new VeiculoEntity();

        when(veiculoMapper.toEntityFromRequestDto(dto)).thenReturn(entity);

        ResponseEntity<VeiculoResponseDTO> response = veiculoService.criarVeiculo(dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }
}
