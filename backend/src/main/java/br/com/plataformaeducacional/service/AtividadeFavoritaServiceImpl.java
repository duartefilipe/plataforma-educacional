package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.dto.AtividadeFavoritaDTO;
import br.com.plataformaeducacional.entity.AtividadeCompartilhada;
import br.com.plataformaeducacional.entity.AtividadeFavorita;
import br.com.plataformaeducacional.entity.User;
import br.com.plataformaeducacional.repository.AtividadeCompartilhadaRepository;
import br.com.plataformaeducacional.repository.AtividadeFavoritaRepository;
import br.com.plataformaeducacional.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AtividadeFavoritaServiceImpl implements AtividadeFavoritaService {
    private final AtividadeFavoritaRepository favoritaRepository;
    private final UserRepository userRepository;
    private final AtividadeCompartilhadaRepository compartilhadaRepository;

    @Override
    public AtividadeFavoritaDTO salvarFavorita(Long professorId, Long atividadeCompartilhadaId) {
        User professor = userRepository.findById(professorId)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado."));
        AtividadeCompartilhada atividade = compartilhadaRepository.findById(atividadeCompartilhadaId)
                .orElseThrow(() -> new EntityNotFoundException("Atividade compartilhada não encontrada."));
        AtividadeFavorita favorita = favoritaRepository.findByProfessorAndAtividadeCompartilhada(professor, atividade)
                .orElse(new AtividadeFavorita(null, professor, atividade));
        favorita = favoritaRepository.save(favorita);
        return toDTO(favorita);
    }

    @Override
    public void removerFavorita(Long professorId, Long atividadeCompartilhadaId) {
        User professor = userRepository.findById(professorId)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado."));
        AtividadeCompartilhada atividade = compartilhadaRepository.findById(atividadeCompartilhadaId)
                .orElseThrow(() -> new EntityNotFoundException("Atividade compartilhada não encontrada."));
        favoritaRepository.findByProfessorAndAtividadeCompartilhada(professor, atividade)
                .ifPresent(favoritaRepository::delete);
    }

    @Override
    public List<AtividadeFavoritaDTO> listarFavoritasPorProfessor(Long professorId) {
        User professor = userRepository.findById(professorId)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado."));
        return favoritaRepository.findByProfessor(professor)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private AtividadeFavoritaDTO toDTO(AtividadeFavorita favorita) {
        AtividadeFavoritaDTO dto = new AtividadeFavoritaDTO();
        dto.setId(favorita.getId());
        dto.setProfessorId(favorita.getProfessor().getId());
        dto.setAtividadeCompartilhadaId(favorita.getAtividadeCompartilhada().getId());
        return dto;
    }
} 