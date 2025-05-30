package com.testeTecnico.revenda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "tb_revenda")
@Data
public class RevendaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotNull
    private String cnpj;

    @Column(nullable = false)
    @NotNull
    private String nome_social;


}
