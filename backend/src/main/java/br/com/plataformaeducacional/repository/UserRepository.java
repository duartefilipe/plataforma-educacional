package br.com.plataformaeducacional.repository;

import br.com.plataformaeducacional.entity.User;
import br.com.plataformaeducacional.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email); 

    boolean existsByEmail(String email);

    List<User> findAllByRole(Role role);

    @Query("SELECT u FROM User u JOIN Professor p ON u.id = p.id JOIN LotacaoProfessor l ON p.id = l.professor.id WHERE l.escola.id = :escolaId AND u.role = 'PROFESSOR'")
    List<User> findProfessoresByEscolaId(Long escolaId);
}