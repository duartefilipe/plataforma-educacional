package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.dto.AtividadeCompartilhadaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface AtividadeCompartilhadaService {

    /**
     * Compartilha uma atividade existente.
     * @param atividadeId ID da atividade a ser compartilhada.
     * @param compartilhamentoDTO Dados do compartilhamento (filtros, tags).
     * @param professorId ID do professor que está compartilhando (deve ser o criador da atividade).
     * @return DTO da atividade compartilhada.
     */
    AtividadeCompartilhadaDTO compartilharAtividade(Long atividadeId, AtividadeCompartilhadaDTO compartilhamentoDTO, Long professorId);

    /**
     * Remove o compartilhamento de uma atividade.
     * @param atividadeId ID da atividade cujo compartilhamento será removido.
     * @param professorId ID do professor (deve ser o criador da atividade).
     */
    void removerCompartilhamento(Long atividadeId, Long professorId);

    /**
     * Busca uma atividade compartilhada pelo seu ID de compartilhamento.
     * @param id ID do registro AtividadeCompartilhada.
     * @return DTO da atividade compartilhada.
     */
    AtividadeCompartilhadaDTO buscarCompartilhamentoPorId(Long id);

     /**
     * Busca uma atividade compartilhada pelo ID da atividade original.
     * @param atividadeId ID da Atividade original.
     * @return DTO da atividade compartilhada, se existir.
     */
    AtividadeCompartilhadaDTO buscarCompartilhamentoPorAtividadeId(Long atividadeId);

    /**
     * Busca atividades compartilhadas com base em filtros e paginação.
     * @param filtros Mapa contendo os critérios de filtro (ex: "disciplina", "anoEscolar", "idadeAlvoMin", "idadeAlvoMax", "tipoAtividade", "queryTexto").
     * @param pageable Objeto de paginação.
     * @return Página de DTOs das atividades compartilhadas encontradas.
     */
    Page<AtividadeCompartilhadaDTO> buscarAtividadesCompartilhadas(Map<String, String> filtros, Pageable pageable);
}

