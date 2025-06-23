package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.dto.TurmaDTO;
import br.com.plataformaeducacional.entity.Escola;
import br.com.plataformaeducacional.entity.Professor;
import br.com.plataformaeducacional.entity.Turma;
import br.com.plataformaeducacional.repository.EscolaRepository;
import br.com.plataformaeducacional.repository.ProfessorRepository;
import br.com.plataformaeducacional.repository.TurmaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TurmaServiceImpl implements TurmaService {

    private final TurmaRepository turmaRepository;
    private final EscolaRepository escolaRepository;
    private final ProfessorRepository professorRepository;

    @Override
    public TurmaDTO createTurma(TurmaDTO turmaDTO) {
        Escola escola = escolaRepository.findById(turmaDTO.getEscolaId())
                .orElseThrow(() -> new EntityNotFoundException("Escola não encontrada com o id: " + turmaDTO.getEscolaId()));

        Professor professor = null;
        if (turmaDTO.getProfessorId() != null) {
            professor = professorRepository.findById(turmaDTO.getProfessorId())
                    .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado com o id: " + turmaDTO.getProfessorId()));
        }

        Turma turma = new Turma();
        turma.setNome(turmaDTO.getNome());
        turma.setAnoLetivo(turmaDTO.getAnoLetivo());
        turma.setEscola(escola);
        turma.setProfessor(professor);

        turma = turmaRepository.save(turma);
        return toDTO(turma);
    }

    @Override
    public List<TurmaDTO> getAllTurmas(Long escolaId) {
        List<Turma> turmas;
        if (escolaId != null) {
            turmas = turmaRepository.findByEscolaId(escolaId);
        } else {
            turmas = turmaRepository.findAll();
        }
        return turmas.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private TurmaDTO toDTO(Turma turma) {
        TurmaDTO dto = new TurmaDTO();
        dto.setId(turma.getId());
        dto.setNome(turma.getNome());
        dto.setAnoLetivo(turma.getAnoLetivo());
        if (turma.getEscola() != null) {
            dto.setEscolaId(turma.getEscola().getId());
            dto.setNomeEscola(turma.getEscola().getNome());
        }
        if (turma.getProfessor() != null && turma.getProfessor().getUser() != null) {
            dto.setProfessorId(turma.getProfessor().getUser().getId());
            dto.setProfessorNome(turma.getProfessor().getUser().getNomeCompleto());
        }
        return dto;
    }
} 