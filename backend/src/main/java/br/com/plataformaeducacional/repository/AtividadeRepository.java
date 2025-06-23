package br.com.plataformaeducacional.repository;

import br.com.plataformaeducacional.entity.Atividade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtividadeRepository extends JpaRepository<Atividade, Long> {

    @Query("SELECT a FROM Atividade a WHERE a.professorCriador.id = :professorUserId")
    List<Atividade> findByProfessorCriadorUserId(Long professorUserId);

}

