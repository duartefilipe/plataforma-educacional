package br.com.plataformaeducacional.repository;

import br.com.plataformaeducacional.entity.AtividadeCompartilhada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AtividadeCompartilhadaRepository extends JpaRepository<AtividadeCompartilhada, Long>, JpaSpecificationExecutor<AtividadeCompartilhada> {
    // JpaSpecificationExecutor permite criar queries dinâmicas para os filtros

    Optional<AtividadeCompartilhada> findByAtividadeId(Long atividadeId);

    boolean existsByAtividadeId(Long atividadeId);

    void deleteByAtividadeId(Long atividadeId);
}

