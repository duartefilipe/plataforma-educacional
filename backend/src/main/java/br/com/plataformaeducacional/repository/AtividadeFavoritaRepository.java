package br.com.plataformaeducacional.repository;

import br.com.plataformaeducacional.entity.AtividadeFavorita;
import br.com.plataformaeducacional.entity.User;
import br.com.plataformaeducacional.entity.AtividadeCompartilhada;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AtividadeFavoritaRepository extends JpaRepository<AtividadeFavorita, Long> {
    List<AtividadeFavorita> findByProfessor(User professor);
    Optional<AtividadeFavorita> findByProfessorAndAtividadeCompartilhada(User professor, AtividadeCompartilhada atividadeCompartilhada);
} 