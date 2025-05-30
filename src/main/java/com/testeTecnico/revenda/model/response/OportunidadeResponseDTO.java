package com.testeTecnico.revenda.model.response;

import com.testeTecnico.revenda.model.enums.StatusOportunidade;

import java.time.LocalDateTime;

public record OportunidadeResponseDTO(
        String message,

        Long id,

        StatusOportunidade status,

        String motivo_conclusao,

        ClienteResponseDTO cliente,

        VeiculoResponseDTO veiculo,

        LocalDateTime data_atribuicao,

        LocalDateTime data_conclusao,

        String responsavel_nome,

        String revenda_nome
) {}
