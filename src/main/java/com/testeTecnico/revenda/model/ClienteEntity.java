package com.testeTecnico.revenda.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_cliente")
@Data
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    private String email;

    private String telefone;
}
