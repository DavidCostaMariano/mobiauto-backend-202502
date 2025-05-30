package com.testeTecnico.revenda.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_veiculo")
@Data
public class VeiculoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String marca;
    private String modelo;
    private String versao;
    private String ano_modelo;
}
