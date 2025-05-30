package com.testeTecnico.revenda.model.request;

public record TransferenciaOportunidadeRequestDTO(
        Long usuario_origem_id,
        Long usuario_destino_id,

        Long oportunidade_id
) {
}
