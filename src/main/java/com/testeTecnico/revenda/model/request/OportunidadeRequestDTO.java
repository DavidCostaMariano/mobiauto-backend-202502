package com.testeTecnico.revenda.model.request;

import com.testeTecnico.revenda.model.enums.StatusOportunidade;

import java.time.LocalDateTime;

public record OportunidadeRequestDTO(
        long id,
        StatusOportunidade status,
        String motivo_conclusao,
        LocalDateTime data_atribuicao,
        LocalDateTime data_conclusao,
        long cliente_id,
        long veiculo_id,
        long revendedora_id,
        long usuario_id
) {}
