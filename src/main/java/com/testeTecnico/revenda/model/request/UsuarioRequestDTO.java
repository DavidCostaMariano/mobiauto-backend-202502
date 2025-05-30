package com.testeTecnico.revenda.model.request;

import com.testeTecnico.revenda.model.enums.CargosEnum;

public record UsuarioRequestDTO(

        long id,
        String nome,
        String email,
        String senha,
        CargosEnum cargo,
        Long revenda_id
) {}
