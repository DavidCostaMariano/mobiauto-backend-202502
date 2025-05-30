package com.testeTecnico.revenda.model.response;

import com.testeTecnico.revenda.model.enums.CargosEnum;

public record UsuarioResponseDTO(
        String message,
        Long id,
        String nome,
        String email,
        CargosEnum cargo,
        String revenda_nome
) {}
