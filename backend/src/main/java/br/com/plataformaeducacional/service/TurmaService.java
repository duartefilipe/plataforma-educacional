package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.dto.TurmaDTO;
import java.util.List;

public interface TurmaService {
    TurmaDTO createTurma(TurmaDTO turmaDTO);
    List<TurmaDTO> getAllTurmas(Long escolaId);
    TurmaDTO updateTurma(Long id, TurmaDTO turmaDTO);
} 