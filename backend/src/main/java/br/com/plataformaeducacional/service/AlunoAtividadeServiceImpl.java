package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.dto.DesignacaoAtividadeDTO;
import br.com.plataformaeducacional.entity.Aluno;
import br.com.plataformaeducacional.entity.Atividade;
import br.com.plataformaeducacional.entity.DesignacaoAtividade;
import br.com.plataformaeducacional.entity.Professor;
import br.com.plataformaeducacional.repository.AlunoRepository;
import br.com.plataformaeducacional.repository.DesignacaoAtividadeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlunoAtividadeServiceImpl implements AlunoAtividadeService {

    private final DesignacaoAtividadeRepository designacaoRepository;
    private final AlunoRepository alunoRepository;

    // Diretório para respostas de alunos. Idealmente, viria de application.properties
    private final Path respostaStorageLocation = Paths.get("/home/ubuntu/plataforma-uploads/respostas-alunos").toAbsolutePath().normalize();

{
    try {
        Files.createDirectories(this.respostaStorageLocation);
    } catch (Exception ex) {
        throw new RuntimeException("Não foi possível criar o diretório para armazenar os arquivos de resposta.", ex);
    }
}


    @Override
    @Transactional(readOnly = true)
    public List<DesignacaoAtividadeDTO> listarAtividadesDesignadasParaAluno(Long alunoId) {
        if (!alunoRepository.existsById(alunoId)) {
            throw new EntityNotFoundException("Aluno não encontrado com ID: " + alunoId);
        }
        List<DesignacaoAtividade> designacoes = designacaoRepository.findByAlunoUserIdOrderByDataDesignacaoDesc(alunoId);
        return designacoes.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DesignacaoAtividadeDTO buscarDetalhesAtividadeDesignada(Long designacaoId, Long alunoId) {
        DesignacaoAtividade designacao = findDesignacaoByIdAndAluno(designacaoId, alunoId);
        return convertToDTO(designacao);
    }

    @Override
    @Transactional
    public DesignacaoAtividadeDTO marcarAtividadeComoVisualizada(Long designacaoId, Long alunoId) {
        DesignacaoAtividade designacao = findDesignacaoByIdAndAluno(designacaoId, alunoId);

        if ("PENDENTE".equals(designacao.getStatus())) {
            designacao.setStatus("VISUALIZADA");
            designacaoRepository.save(designacao);
        }
        return convertToDTO(designacao);
    }

    @Override
    @Transactional
    public DesignacaoAtividadeDTO submeterRespostaAtividade(Long designacaoId, Long alunoId, String respostaTexto, MultipartFile arquivoResposta) throws IOException {
        DesignacaoAtividade designacao = findDesignacaoByIdAndAluno(designacaoId, alunoId);

        if (!("PENDENTE".equals(designacao.getStatus()) || "VISUALIZADA".equals(designacao.getStatus()))) {
            throw new IllegalStateException("Não é possível submeter resposta para uma atividade com status: " + designacao.getStatus());
        }

        designacao.setRespostaAlunoTexto(respostaTexto);

        // Lida com o upload do arquivo de resposta
        if (arquivoResposta != null && !arquivoResposta.isEmpty()) {
            // Deleta arquivo de resposta antigo, se existir
            deleteArquivoFisico(designacao.getRespostaAlunoArquivo());

            // Salva novo arquivo
            String originalFilename = arquivoResposta.getOriginalFilename() != null ? arquivoResposta.getOriginalFilename() : "resposta";
            String fileExtension = "";
            int lastDot = originalFilename.lastIndexOf(".");
            if (lastDot > 0) {
                fileExtension = originalFilename.substring(lastDot);
            }
            String storedFilename = "aluno_" + alunoId + "_designacao_" + designacaoId + "_" + UUID.randomUUID().toString() + fileExtension;
            Path targetLocation = this.respostaStorageLocation.resolve(storedFilename);
            Files.copy(arquivoResposta.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            designacao.setRespostaAlunoArquivo(targetLocation.toString());
        } else {
             // Se não veio arquivo novo, remove o antigo (caso exista)
             deleteArquivoFisico(designacao.getRespostaAlunoArquivo());
             designacao.setRespostaAlunoArquivo(null);
        }

        designacao.setStatus("ENTREGUE");
        designacao.setDataEntrega(LocalDateTime.now());

        DesignacaoAtividade savedDesignacao = designacaoRepository.save(designacao);
        return convertToDTO(savedDesignacao);
    }

    // --- Métodos Auxiliares ---

    private DesignacaoAtividade findDesignacaoByIdAndAluno(Long designacaoId, Long alunoId) {
        DesignacaoAtividade designacao = designacaoRepository.findById(designacaoId)
                .orElseThrow(() -> new EntityNotFoundException("Designação de atividade não encontrada com ID: " + designacaoId));

        if (!designacao.getAluno().getUserId().equals(alunoId)) {
            throw new AccessDeniedException("Aluno não tem permissão para acessar esta designação.");
        }
        return designacao;
    }

    private void deleteArquivoFisico(String caminhoArquivo) throws IOException {
        if (caminhoArquivo != null && !caminhoArquivo.isBlank()) {
            Path path = Paths.get(caminhoArquivo);
            if (Files.exists(path)) {
                Files.delete(path);
            }
        }
    }

    private DesignacaoAtividadeDTO convertToDTO(DesignacaoAtividade designacao) {
        DesignacaoAtividadeDTO dto = new DesignacaoAtividadeDTO();
        BeanUtils.copyProperties(designacao, dto);

        if (designacao.getAtividade() != null) {
            Atividade atividade = designacao.getAtividade();
            dto.setAtividadeId(atividade.getId());
            dto.setAtividadeTitulo(atividade.getTitulo());
            dto.setAtividadeDescricao(atividade.getDescricao());
            dto.setAtividadeTipoConteudo(atividade.getTipoConteudo());
            dto.setAtividadeNomeArquivoOriginal(atividade.getNomeArquivoOriginal()); // Para link de download da atividade original
        }

        if (designacao.getProfessorDesignador() != null) {
            Professor professor = designacao.getProfessorDesignador();
            dto.setProfessorDesignadorId(professor.getUserId());
            if (professor.getUser() != null) {
                dto.setProfessorDesignadorNome(professor.getUser().getNomeCompleto());
            } else {
                 dto.setProfessorDesignadorNome("Professor ID: " + professor.getUserId());
            }
        }

        // Não incluir caminho do arquivo de resposta no DTO
        dto.setRespostaAlunoArquivo(null);

        return dto;
    }
}

