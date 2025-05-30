package com.testeTecnico.revenda;

import com.testeTecnico.revenda.model.ClienteEntity;
import com.testeTecnico.revenda.model.VeiculoEntity;
import com.testeTecnico.revenda.model.request.ClienteRequestDTO;
import com.testeTecnico.revenda.model.request.VeiculoRequestDTO;
import com.testeTecnico.revenda.model.response.ClienteResponseDTO;
import com.testeTecnico.revenda.model.response.VeiculoResponseDTO;
import com.testeTecnico.revenda.repository.ClienteRepository;
import com.testeTecnico.revenda.repository.VeiculoRepository;
import com.testeTecnico.revenda.service.ClienteService;
import com.testeTecnico.revenda.service.VeiculoService;
import com.testeTecnico.revenda.util.mapper.ClienteMapper;
import com.testeTecnico.revenda.util.mapper.VeiculoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {


    @InjectMocks
    private ClienteService clienteService;


    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @Test
    void deveCriarClienteComSucesso() {
        ClienteRequestDTO dto = new ClienteRequestDTO(0l, "usuario teste","emailusuario@gmail.com","11123456789");
        ClienteEntity entity = new ClienteEntity();

        when(clienteMapper.toEntityFromRequestDto(dto)).thenReturn(entity);

        ResponseEntity<ClienteResponseDTO> response = clienteService.criarCliente(dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }
}