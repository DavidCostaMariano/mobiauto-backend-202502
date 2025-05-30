package com.testeTecnico.revenda.model.enums;

import lombok.Data;

public enum StatusOportunidade {

    NOVO("NOVO", "PEDIDO ABERTO"),

    EM_ATENDIMENTO("EM ATENDIMENTO", "EM ATENDIMENTO"),

    CONCLUIDO("FINALIZADO", "VENDA EFETUADA");

    private final String status;
    private final String mensagem;

    StatusOportunidade(String status, String mensagem) {
        this.status = status;
        this.mensagem = mensagem;
    }

    public String getStatus() {
        return status;
    }

    public String getMensagem() {
        return mensagem;
    }
}

