package br.com.plataformaeducacional.repository;

import br.com.plataformaeducacional.entity.MatriculaAluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatriculaAlunoRepository extends JpaRepository<MatriculaAluno, Long> {

    List<MatriculaAluno> findByAlunoId(Long alunoId);

    List<MatriculaAluno> findByEscolaId(Long escolaId);

    List<MatriculaAluno> findByTurmaId(Long turmaId);

    Optional<MatriculaAluno> findByAlunoIdAndEscolaIdAndTurnoAndAnoLetivo(Long alunoId, Long escolaId, String turno, Integer anoLetivo);

    void deleteByAlunoIdAndEscolaIdAndTurnoAndAnoLetivo(Long alunoId, Long escolaId, String turno, Integer anoLetivo);
}

