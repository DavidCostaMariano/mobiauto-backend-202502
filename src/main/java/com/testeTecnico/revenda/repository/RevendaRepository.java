package com.testeTecnico.revenda.repository;
import com.testeTecnico.revenda.model.RevendaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RevendaRepository extends JpaRepository<RevendaEntity, Long> {

    RevendaEntity findByCnpj(String cnpj);
}
