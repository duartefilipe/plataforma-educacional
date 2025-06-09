package br.com.plataformaeducacional.repository;

import br.com.plataformaeducacional.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    // userId é a chave primária de Professor, que é a FK para User
}

