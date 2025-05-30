package com.testeTecnico.revenda.model;

import com.testeTecnico.revenda.model.enums.StatusOportunidade;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_oportunidade")
@Data
public class OportunidadeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatusOportunidade status;

    private String motivo_conclusao;

    private LocalDateTime data_atribuicao;
    private LocalDateTime data_conclusao;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;

    @OneToOne
    @JoinColumn(name = "veiculo_id")
    private VeiculoEntity veiculo;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private UsuarioEntity usuario;

    @ManyToOne
    @JoinColumn(name = "revenda_id")
    private RevendaEntity revenda;

}
