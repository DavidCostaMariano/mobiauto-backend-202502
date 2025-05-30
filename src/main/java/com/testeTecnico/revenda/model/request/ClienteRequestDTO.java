package com.testeTecnico.revenda.model.request;

public record ClienteRequestDTO(
        long id,
        String nome,
        String email,
        String telefone
) {
}
