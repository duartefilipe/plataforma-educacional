package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.dto.EscolaDTO;
import java.util.List;
import java.util.Optional;

public interface EscolaService {
    EscolaDTO createEscola(EscolaDTO escolaDTO);
    List<EscolaDTO> getAllEscolas();
    Optional<EscolaDTO> getEscolaById(Long id);
    EscolaDTO atualizarEscola(Long id, EscolaDTO escolaDTO);
    void deleteEscola(Long id);
} 