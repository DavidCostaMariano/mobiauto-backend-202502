package com.testeTecnico.revenda.model.request;

import jakarta.validation.constraints.NotNull;

public record RevendaRequestDTO(

        @NotNull
        String cnpj,

        @NotNull
        String nome_social
) {}
