package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.dto.AtividadeDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AtividadeService {

    /**
     * Cria uma nova atividade, podendo incluir um arquivo.
     * @param atividadeDTO Dados da atividade.
     * @param arquivo (Opcional) Arquivo a ser anexado.
     * @param professorId ID do professor criando a atividade.
     * @return DTO da atividade criada.
     * @throws IOException Se houver erro no upload do arquivo.
     */
    AtividadeDTO criarAtividade(AtividadeDTO atividadeDTO, MultipartFile arquivo, Long professorId) throws IOException;

    /**
     * Busca uma atividade pelo ID.
     * @param id ID da atividade.
     * @param professorId ID do professor (para verificar permissão).
     * @return DTO da atividade encontrada.
     */
    AtividadeDTO buscarAtividadePorId(Long id, Long professorId);

    /**
     * Lista todas as atividades criadas por um professor específico.
     * @param professorId ID do professor.
     * @return Lista de DTOs das atividades.
     */
    List<AtividadeDTO> listarAtividadesPorProfessor(Long professorId);

    /**
     * Atualiza uma atividade existente.
     * @param id ID da atividade a ser atualizada.
     * @param atividadeDTO Novos dados da atividade.
     * @param arquivo (Opcional) Novo arquivo a ser anexado (substitui o antigo se existir).
     * @param professorId ID do professor (para verificar permissão).
     * @return DTO da atividade atualizada.
     * @throws IOException Se houver erro no upload do arquivo.
     */
    AtividadeDTO atualizarAtividade(Long id, AtividadeDTO atividadeDTO, MultipartFile arquivo, Long professorId) throws IOException;

    /**
     * Deleta uma atividade.
     * @param id ID da atividade a ser deletada.
     * @param professorId ID do professor (para verificar permissão).
     * @throws IOException Se houver erro ao deletar arquivo associado.
     */
    void deletarAtividade(Long id, Long professorId) throws IOException;

    // Métodos para download podem ser adicionados aqui ou no controller
    // Ex: byte[] baixarArquivoAtividade(Long id, Long professorId) throws IOException;
    // Ex: Atividade getAtividadeParaDownload(Long id, Long professorId);
}

