package br.com.plataformaeducacional.repository;

import br.com.plataformaeducacional.entity.DesignacaoAtividade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DesignacaoAtividadeRepository extends JpaRepository<DesignacaoAtividade, Long> {

    List<DesignacaoAtividade> findByAlunoUserIdOrderByDataDesignacaoDesc(Long alunoUserId);

    List<DesignacaoAtividade> findByProfessorDesignadorUserId(Long professorId);

    List<DesignacaoAtividade> findByAtividadeId(Long atividadeId);

    Optional<DesignacaoAtividade> findByAtividadeIdAndAlunoUserId(Long atividadeId, Long alunoUserId);

    // Query para buscar atividades relevantes para um aluno:
    // 1. Atividades designadas diretamente a ele
    // 2. Atividades compartilhadas que podem ser relevantes (lógica mais complexa, talvez fora do escopo inicial ou em outro serviço)
    // Por enquanto, focamos nas designadas diretamente.

    // Exemplo de query para buscar atividades de escolas onde o aluno está matriculado (requer joins complexos)
    // Esta lógica pode ser melhor implementada no Service layer, combinando resultados.
    /*
    @Query("SELECT da FROM DesignacaoAtividade da JOIN da.atividade a JOIN a.professorCriador pc JOIN pc.lotacoes l JOIN l.escola e JOIN e.matriculas m WHERE m.aluno.userId = :alunoId")
    List<DesignacaoAtividade> findAtividadesDasEscolasDoAluno(Long alunoId);
    */
}

