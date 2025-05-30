package com.testeTecnico.revenda.model.response;

public record VeiculoResponseDTO(

        String message,
        String marca,
        String modelo,
        String versao,
        String ano_modelo
) {
}
