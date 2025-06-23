package br.com.plataformaeducacional.repository;

import br.com.plataformaeducacional.entity.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {
    List<Turma> findByEscolaId(Long escolaId);
}