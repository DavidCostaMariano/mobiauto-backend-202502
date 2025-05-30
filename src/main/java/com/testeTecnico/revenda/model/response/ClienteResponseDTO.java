package com.testeTecnico.revenda.model.response;

public record ClienteResponseDTO(
        String nome,
        String email,
        String telefone
) {
}
