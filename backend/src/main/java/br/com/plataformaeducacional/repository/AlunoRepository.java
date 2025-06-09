package br.com.plataformaeducacional.repository;

import br.com.plataformaeducacional.entity.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    // userId é a chave primária de Aluno, que é a FK para User
}

