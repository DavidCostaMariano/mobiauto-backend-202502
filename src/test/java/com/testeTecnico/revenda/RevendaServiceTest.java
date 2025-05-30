package com.testeTecnico.revenda;

import com.testeTecnico.revenda.model.RevendaEntity;
import com.testeTecnico.revenda.model.request.RevendaRequestDTO;
import com.testeTecnico.revenda.model.response.RevendaResponseDTO;
import com.testeTecnico.revenda.repository.RevendaRepository;
import com.testeTecnico.revenda.service.RevendaService;
import com.testeTecnico.revenda.util.mapper.RevendaMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RevendaServiceTest {

    @InjectMocks
    private RevendaService service;

    @Mock
    private RevendaRepository revendaRepository;

    @Mock
    private RevendaMapper revendaMapper;

    @Test
    void deveCriarRevendaComSucesso() throws Exception {
        RevendaRequestDTO dto = new RevendaRequestDTO("83653603000110", "Revenda Teste");
        RevendaEntity revendaEntity = new RevendaEntity();

        when(revendaRepository.findByCnpj(dto.cnpj())).thenReturn(null);
        when(revendaMapper.toEntityFromRequestDto(dto)).thenReturn(revendaEntity);
        when(revendaMapper.toResponseDto(anyString(), eq(revendaEntity))).thenReturn(new RevendaResponseDTO("Revenda cadastrada com sucesso", null, null, null));

        ResponseEntity<RevendaResponseDTO> response = service.criarRevenda(dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().message()).isEqualTo("Revenda cadastrada com sucesso");
    }

    @Test
    void deveRetornarErroSeCnpjInvalido() throws Exception {
        RevendaRequestDTO dto = new RevendaRequestDTO("11111111111111", "Revenda Invalida");

        ResponseEntity<RevendaResponseDTO> response = service.criarRevenda(dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("CNPJ INVALIDO");
    }

    @Test
    void deveRetornarErroSeCnpjJaExisteNaBase() throws Exception {
        RevendaRequestDTO dto = new RevendaRequestDTO("83653603000110", "Revenda Existente");
        RevendaEntity existente = new RevendaEntity();

        when(revendaRepository.findByCnpj(dto.cnpj())).thenReturn(existente);

        ResponseEntity<RevendaResponseDTO> response = service.criarRevenda(dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("CNPJ PRESENTE NA BASE");
    }
}