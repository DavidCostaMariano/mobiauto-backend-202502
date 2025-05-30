package com.testeTecnico.revenda.model.response;

public record RevendaResponseDTO(
        String message,
        Long id,
        String cnpj,
        String nome_social
) {}

