package br.com.plataformaeducacional.repository;

import br.com.plataformaeducacional.entity.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    List<Tarefa> findByAlunoIdOrderByDataDesignacaoDesc(Long alunoId);

    @Query("SELECT t FROM Tarefa t WHERE t.professorDesignador.id = :professorId")
    List<Tarefa> findByProfessorDesignadorUserId(Long professorId);

    List<Tarefa> findByAtividadeId(Long atividadeId);

    Optional<Tarefa> findByAtividadeIdAndAlunoId(Long atividadeId, Long alunoId);

    boolean existsByAtividadeIdAndAlunoId(Long atividadeId, Long alunoId);

    // Query para buscar atividades relevantes para um aluno:
    // 1. Atividades designadas diretamente a ele
    // 2. Atividades compartilhadas que podem ser relevantes (lógica mais complexa, talvez fora do escopo inicial ou em outro serviço)
    // Por enquanto, focamos nas designadas diretamente.

    // Exemplo de query para buscar atividades de escolas onde o aluno está matriculado (requer joins complexos)
    // Esta lógica pode ser melhor implementada no Service layer, combinando resultados.
    @Query("SELECT t FROM Tarefa t JOIN t.aluno a JOIN a.matriculas m JOIN m.turma turma JOIN turma.escola e WHERE a.id = :alunoId")
    List<Tarefa> findTarefasDasEscolasDoAluno(Long alunoId);
}

