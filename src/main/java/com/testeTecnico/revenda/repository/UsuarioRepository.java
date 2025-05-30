package com.testeTecnico.revenda.repository;

import com.testeTecnico.revenda.model.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmailIgnoreCase(String email);
    UserDetails findByEmail(String email);

    @NativeQuery("""
            SELECT tu.*
            FROM tb_controle_vendas tcv
            LEFT JOIN tb_usuario tu ON tcv.usuario_id = tu.id
            WHERE tu.revenda_id = :revenda_id
            ORDER BY tcv.numero_revendas_atuais ASC, tcv.data_ultima_oportunidade ASC
            LIMIT 1
            """)
    UsuarioEntity recuperarUsuarioComMenorNumeroDeVendasAtuais(@Param("revenda_id") long revendaId);

}
