package com.testeTecnico.revenda.config;

import com.testeTecnico.revenda.model.*;
import com.testeTecnico.revenda.model.enums.CargosEnum;
import com.testeTecnico.revenda.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            UsuarioRepository usuarioRepository,
            RevendaRepository revendaRepository,
            ClienteRepository clienteRepository,
            VeiculoRepository veiculoRepository,
            ControleVendasRepository controleVendasRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            UsuarioEntity admin = new UsuarioEntity();
            if (revendaRepository.count() == 0) {
                RevendaEntity revenda = new RevendaEntity();
                revenda.setCnpj("12345678000199");
                revenda.setNome_social("Revenda Oficial");
                revenda = revendaRepository.save(revenda);
                admin.setNome("Administrador");
                admin.setEmail("admin@revenda.com");
                admin.setSenha(passwordEncoder.encode("adminPassword123"));
                admin.setCargo(CargosEnum.ADMINISTRADOR);
                admin.setRevenda(revenda);
                usuarioRepository.save(admin);
            }

            if (clienteRepository.count() == 0) {
                ClienteEntity cliente = new ClienteEntity();
                cliente.setNome("João Silva");
                cliente.setEmail("joao@email.com");
                cliente.setTelefone("11999999999");
                clienteRepository.save(cliente);
            }

            // Cria veículo
            if (veiculoRepository.count() == 0) {
                VeiculoEntity veiculo = new VeiculoEntity();
                veiculo.setMarca("Fiat");
                veiculo.setModelo("Uno");
                veiculo.setVersao("Mille Economy");
                veiculo.setAno_modelo("2013");
                veiculoRepository.save(veiculo);
            }

            if (controleVendasRepository.count() == 0) {
                ControleVendasEntity vendas = new ControleVendasEntity();
                vendas.setUsuario(admin);
                vendas.setNumero_revendas_atuais(0);
                vendas.setData_ultima_oportunidade(null);
                controleVendasRepository.save(vendas);
            }


            System.out.println("Dados iniciais inseridos com sucesso.");
        };
    }
}
