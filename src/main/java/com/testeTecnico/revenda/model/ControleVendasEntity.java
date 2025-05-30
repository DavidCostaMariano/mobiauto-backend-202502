package com.testeTecnico.revenda.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_controle_vendas")
@Data
public class ControleVendasEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    private int numero_revendas_atuais;

    private LocalDateTime data_ultima_oportunidade;
}
