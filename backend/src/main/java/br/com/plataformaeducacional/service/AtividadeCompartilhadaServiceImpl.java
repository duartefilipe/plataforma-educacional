package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.dto.AtividadeCompartilhadaDTO;
import br.com.plataformaeducacional.entity.Atividade;
import br.com.plataformaeducacional.entity.AtividadeCompartilhada;
import br.com.plataformaeducacional.entity.Professor;
import br.com.plataformaeducacional.repository.AtividadeCompartilhadaRepository;
import br.com.plataformaeducacional.repository.AtividadeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AtividadeCompartilhadaServiceImpl implements AtividadeCompartilhadaService {

    private final AtividadeCompartilhadaRepository atividadeCompartilhadaRepository;
    private final AtividadeRepository atividadeRepository;
    // Não precisamos do ProfessorRepository aqui diretamente, pois a validação é feita via Atividade

    @Override
    @Transactional
    public AtividadeCompartilhadaDTO compartilharAtividade(Long atividadeId, AtividadeCompartilhadaDTO compartilhamentoDTO, Long professorId) {
        Atividade atividade = atividadeRepository.findById(atividadeId)
                .orElseThrow(() -> new EntityNotFoundException("Atividade não encontrada com ID: " + atividadeId));

        // Validar se o professor logado é o criador da atividade
        if (!atividade.getProfessorCriador().getId().equals(professorId)) {
            throw new AccessDeniedException("Apenas o criador pode compartilhar a atividade.");
        }

        // Validar se já não está compartilhada
        if (atividadeCompartilhadaRepository.existsByAtividadeId(atividadeId)) {
            throw new IllegalStateException("Esta atividade já está compartilhada.");
        }

        AtividadeCompartilhada novaCompartilhada = new AtividadeCompartilhada();
        novaCompartilhada.setAtividade(atividade);
        // Copia os dados de filtro/categorização do DTO
        BeanUtils.copyProperties(compartilhamentoDTO, novaCompartilhada, "id", "atividadeId", "dataCompartilhamento", "atividadeTitulo", "atividadeDescricao", "atividadeTipoConteudo", "atividadeProfessorCriadorId", "atividadeProfessorCriadorNome", "atividadeCreatedAt");

        AtividadeCompartilhada saved = atividadeCompartilhadaRepository.save(novaCompartilhada);
        return convertToDTO(saved);
    }

    @Override
    @Transactional
    public void removerCompartilhamento(Long atividadeId, Long professorId) {
        Atividade atividade = atividadeRepository.findById(atividadeId)
                .orElseThrow(() -> new EntityNotFoundException("Atividade não encontrada com ID: " + atividadeId));

        // Validar se o professor logado é o criador da atividade
        if (!atividade.getProfessorCriador().getId().equals(professorId)) {
            throw new AccessDeniedException("Apenas o criador pode remover o compartilhamento da atividade.");
        }

        // Encontrar e deletar o registro de compartilhamento
        // Usar deleteByAtividadeId para evitar busca extra se não precisar do objeto
        if (!atividadeCompartilhadaRepository.existsByAtividadeId(atividadeId)) {
             throw new EntityNotFoundException("Atividade não está compartilhada.");
        }
        atividadeCompartilhadaRepository.deleteByAtividadeId(atividadeId);
    }

    @Override
    @Transactional(readOnly = true)
    public AtividadeCompartilhadaDTO buscarCompartilhamentoPorId(Long id) {
        AtividadeCompartilhada compartilhada = atividadeCompartilhadaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Compartilhamento não encontrado com ID: " + id));
        return convertToDTO(compartilhada);
    }

    @Override
    @Transactional(readOnly = true)
    public AtividadeCompartilhadaDTO buscarCompartilhamentoPorAtividadeId(Long atividadeId) {
        AtividadeCompartilhada compartilhada = atividadeCompartilhadaRepository.findByAtividadeId(atividadeId)
                .orElseThrow(() -> new EntityNotFoundException("Nenhum compartilhamento encontrado para a atividade ID: " + atividadeId));
        return convertToDTO(compartilhada);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AtividadeCompartilhadaDTO> buscarAtividadesCompartilhadas(Map<String, String> filtros, Pageable pageable) {
        Specification<AtividadeCompartilhada> spec = buildSpecification(filtros);
        Page<AtividadeCompartilhada> page = atividadeCompartilhadaRepository.findAll(spec, pageable);
        return page.map(this::convertToDTO); // Converte a página de entidades para DTOs
    }

    // --- Métodos Auxiliares ---

    private Specification<AtividadeCompartilhada> buildSpecification(Map<String, String> filtros) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Join com Atividade para filtros no título/descrição
            jakarta.persistence.criteria.Join<AtividadeCompartilhada, Atividade> atividadeJoin = root.join("atividade");

            filtros.forEach((key, value) -> {
                if (value != null && !value.isBlank()) {
                    switch (key) {
                        case "disciplina":
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("disciplina")), "%" + value.toLowerCase() + "%"));
                            break;
                        case "anoEscolar":
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("anoEscolar")), "%" + value.toLowerCase() + "%"));
                            break;
                        case "tipoAtividade":
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("tipoAtividade")), "%" + value.toLowerCase() + "%"));
                            break;
                        case "idadeAlvoMin":
                            try {
                                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("idadeAlvoMin"), Integer.parseInt(value)));
                            } catch (NumberFormatException e) { /* Ignora filtro inválido */ }
                            break;
                        case "idadeAlvoMax":
                            try {
                                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("idadeAlvoMax"), Integer.parseInt(value)));
                            } catch (NumberFormatException e) { /* Ignora filtro inválido */ }
                            break;
                        case "tags":
                             predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("tags")), "%" + value.toLowerCase() + "%"));
                             break;
                        case "queryTexto": // Busca no título ou descrição da atividade original
                            Predicate tituloLike = criteriaBuilder.like(criteriaBuilder.lower(atividadeJoin.get("titulo")), "%" + value.toLowerCase() + "%");
                            Predicate descricaoLike = criteriaBuilder.like(criteriaBuilder.lower(atividadeJoin.get("descricao")), "%" + value.toLowerCase() + "%");
                            predicates.add(criteriaBuilder.or(tituloLike, descricaoLike));
                            break;
                        // Adicionar mais filtros conforme necessário
                    }
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private AtividadeCompartilhadaDTO convertToDTO(AtividadeCompartilhada compartilhada) {
        AtividadeCompartilhadaDTO dto = new AtividadeCompartilhadaDTO();
        BeanUtils.copyProperties(compartilhada, dto);

        // Preenche dados da atividade original
        if (compartilhada.getAtividade() != null) {
            Atividade atividade = compartilhada.getAtividade();
            dto.setAtividadeId(atividade.getId());
            dto.setAtividadeTitulo(atividade.getTitulo());
            dto.setAtividadeDescricao(atividade.getDescricao());
            dto.setAtividadeTipoConteudo(atividade.getTipoConteudo());
            dto.setAtividadeCreatedAt(atividade.getCreatedAt());

            if (atividade.getProfessorCriador() != null) {
                dto.setAtividadeProfessorCriadorId(atividade.getProfessorCriador().getId());
                dto.setAtividadeProfessorCriadorNome(atividade.getProfessorCriador().getNomeCompleto());
            }
            if (atividade.getEscola() != null) {
                dto.setEscolaNome(atividade.getEscola().getNome());
            }
        }
        return dto;
    }
}

