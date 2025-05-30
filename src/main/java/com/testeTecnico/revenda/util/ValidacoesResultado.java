package com.testeTecnico.revenda.util;

import com.testeTecnico.revenda.model.ClienteEntity;
import com.testeTecnico.revenda.model.RevendaEntity;
import com.testeTecnico.revenda.model.UsuarioEntity;
import com.testeTecnico.revenda.model.VeiculoEntity;

public record ValidacoesResultado (
        ClienteEntity cliente,
        VeiculoEntity veiculo,
        RevendaEntity revenda,
        UsuarioEntity usuario){
}
