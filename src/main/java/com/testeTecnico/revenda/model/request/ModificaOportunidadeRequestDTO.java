package com.testeTecnico.revenda.model.request;

public record ModificaOportunidadeRequestDTO(
        Long oportunidade_id,
        Long cliente_id,
        Long usuario_id,
        Long veiculo_id
) {
}
