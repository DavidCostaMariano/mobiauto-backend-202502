package com.testeTecnico.revenda.model.request;

import lombok.Data;

public record RevendaRequestDTO(

        String cnpj,
        String nome_social
) {}
