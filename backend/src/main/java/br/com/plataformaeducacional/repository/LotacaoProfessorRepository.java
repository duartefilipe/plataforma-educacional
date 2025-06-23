package br.com.plataformaeducacional.repository;

import br.com.plataformaeducacional.entity.LotacaoProfessor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LotacaoProfessorRepository extends JpaRepository<LotacaoProfessor, Long> {

    @Query("SELECT l FROM LotacaoProfessor l WHERE l.professor.id = :professorId")
    List<LotacaoProfessor> findByProfessorId(Long professorId);

    List<LotacaoProfessor> findByEscolaId(Long escolaId);

    @Modifying
    @Query("DELETE FROM LotacaoProfessor l WHERE l.professor.id = :professorId AND l.escola.id = :escolaId")
    void deleteByProfessorIdAndEscolaId(Long professorId, Long escolaId);

    boolean existsByProfessorIdAndEscolaId(Long professorId, Long escolaId);
}

