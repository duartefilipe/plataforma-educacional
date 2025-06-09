package br.com.plataformaeducacional.repository;

import br.com.plataformaeducacional.entity.Atividade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtividadeRepository extends JpaRepository<Atividade, Long> {

    List<Atividade> findByProfessorCriadorUserId(Long professorUserId);

}

