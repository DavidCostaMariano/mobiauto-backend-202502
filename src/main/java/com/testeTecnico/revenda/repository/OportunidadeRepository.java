package com.testeTecnico.revenda.repository;

import com.testeTecnico.revenda.model.OportunidadeEntity;
import com.testeTecnico.revenda.model.enums.StatusOportunidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OportunidadeRepository extends JpaRepository<OportunidadeEntity, Long> {

    List<OportunidadeEntity> findAllByRevendaId(long id);

    List<OportunidadeEntity> findAllByRevendaIdAndStatusNot(long id, StatusOportunidade statusOportunidade);
}
