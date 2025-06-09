package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.dto.DesignacaoAtividadeDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AlunoAtividadeService {

    /**
     * Lista todas as atividades designadas para um aluno específico.
     * @param alunoId ID do aluno.
     * @return Lista de DTOs das atividades designadas.
     */
    List<DesignacaoAtividadeDTO> listarAtividadesDesignadasParaAluno(Long alunoId);

    /**
     * Busca detalhes de uma atividade designada específica para o aluno.
     * @param designacaoId ID da designação.
     * @param alunoId ID do aluno (para verificar permissão).
     * @return DTO da atividade designada.
     */
    DesignacaoAtividadeDTO buscarDetalhesAtividadeDesignada(Long designacaoId, Long alunoId);

    /**
     * Marca uma atividade designada como visualizada pelo aluno.
     * @param designacaoId ID da designação.
     * @param alunoId ID do aluno.
     * @return DTO da atividade designada atualizada.
     */
    DesignacaoAtividadeDTO marcarAtividadeComoVisualizada(Long designacaoId, Long alunoId);

    /**
     * Permite ao aluno submeter uma resposta (texto ou arquivo) para uma atividade designada.
     * @param designacaoId ID da designação.
     * @param alunoId ID do aluno.
     * @param respostaTexto (Opcional) Texto da resposta.
     * @param arquivoResposta (Opcional) Arquivo de resposta.
     * @return DTO da atividade designada atualizada.
     * @throws IOException Se houver erro no upload do arquivo de resposta.
     */
    DesignacaoAtividadeDTO submeterRespostaAtividade(Long designacaoId, Long alunoId, String respostaTexto, MultipartFile arquivoResposta) throws IOException;

    // Poderia haver um método para listar atividades gerais da escola, mas a lógica é mais complexa
    // List<AtividadeGeralDTO> listarAtividadesGeraisDaEscola(Long alunoId);

}

