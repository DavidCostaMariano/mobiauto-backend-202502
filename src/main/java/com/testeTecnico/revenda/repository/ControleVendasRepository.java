package com.testeTecnico.revenda.repository;

import com.testeTecnico.revenda.model.ControleVendasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ControleVendasRepository extends JpaRepository<ControleVendasEntity, Long> {

    ControleVendasEntity findByUsuarioId(long usuarioId);



}
