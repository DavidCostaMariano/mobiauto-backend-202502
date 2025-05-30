package com.testeTecnico.revenda.model.request;

public record VeiculoRequestDTO(
     long id,
     String marca,
     String modelo,
     String versao,
     String ano_modelo
) {
}
