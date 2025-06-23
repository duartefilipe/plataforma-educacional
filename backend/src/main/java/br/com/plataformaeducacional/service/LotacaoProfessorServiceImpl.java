package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.entity.LotacaoProfessor;
import br.com.plataformaeducacional.entity.Professor;
import br.com.plataformaeducacional.entity.Escola;
import br.com.plataformaeducacional.repository.LotacaoProfessorRepository;
import br.com.plataformaeducacional.repository.ProfessorRepository;
import br.com.plataformaeducacional.repository.EscolaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LotacaoProfessorServiceImpl implements LotacaoProfessorService {
    private final LotacaoProfessorRepository lotacaoProfessorRepository;
    private final ProfessorRepository professorRepository;
    private final EscolaRepository escolaRepository;

    @Override
    @Transactional
    public void vincularProfessorEscola(Long professorId, Long escolaId) {
        if (!lotacaoProfessorRepository.existsByProfessorIdAndEscolaId(professorId, escolaId)) {
            Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
            Escola escola = escolaRepository.findById(escolaId)
                .orElseThrow(() -> new RuntimeException("Escola não encontrada"));
            LotacaoProfessor lotacao = new LotacaoProfessor();
            lotacao.setProfessor(professor);
            lotacao.setEscola(escola);
            lotacaoProfessorRepository.save(lotacao);
        }
    }
} 