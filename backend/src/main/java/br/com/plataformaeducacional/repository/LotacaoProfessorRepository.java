package br.com.plataformaeducacional.repository;

import br.com.plataformaeducacional.entity.LotacaoProfessor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LotacaoProfessorRepository extends JpaRepository<LotacaoProfessor, Long> {

    List<LotacaoProfessor> findByProfessorUserId(Long professorUserId);

    List<LotacaoProfessor> findByEscolaId(Long escolaId);

    void deleteByProfessorUserIdAndEscolaId(Long professorUserId, Long escolaId);
}

