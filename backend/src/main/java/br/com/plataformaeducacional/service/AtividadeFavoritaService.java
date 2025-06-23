package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.dto.AtividadeFavoritaDTO;
import java.util.List;

public interface AtividadeFavoritaService {
    AtividadeFavoritaDTO salvarFavorita(Long professorId, Long atividadeCompartilhadaId);
    void removerFavorita(Long professorId, Long atividadeCompartilhadaId);
    List<AtividadeFavoritaDTO> listarFavoritasPorProfessor(Long professorId);
} 