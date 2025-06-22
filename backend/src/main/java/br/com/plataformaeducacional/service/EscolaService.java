package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.dto.EscolaDTO;
import java.util.List;

public interface EscolaService {
    EscolaDTO criarEscola(EscolaDTO escolaDTO);
    List<EscolaDTO> listarEscolas();
    EscolaDTO buscarEscolaPorId(Long id);
    EscolaDTO atualizarEscola(Long id, EscolaDTO escolaDTO);
    void deletarEscola(Long id);
} 