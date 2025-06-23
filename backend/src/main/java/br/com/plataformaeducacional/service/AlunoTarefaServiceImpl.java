package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.dto.TarefaDTO;
import br.com.plataformaeducacional.entity.Aluno;
import br.com.plataformaeducacional.entity.Atividade;
import br.com.plataformaeducacional.entity.Tarefa;
import br.com.plataformaeducacional.entity.Professor;
import br.com.plataformaeducacional.repository.AlunoRepository;
import br.com.plataformaeducacional.repository.TarefaRepository;
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
public class AlunoTarefaServiceImpl implements AlunoTarefaService {

    private final TarefaRepository tarefaRepository;
    private final AlunoRepository alunoRepository;

    // Diretório para respostas de alunos. Idealmente, viria de application.properties
    private final Path respostaStorageLocation = Paths.get("uploads/respostas-alunos").toAbsolutePath().normalize();

{
    try {
        Files.createDirectories(this.respostaStorageLocation);
    } catch (Exception ex) {
        throw new RuntimeException("Não foi possível criar o diretório para armazenar os arquivos de resposta.", ex);
    }
}


    @Override
    @Transactional(readOnly = true)
    public List<TarefaDTO> listarTarefasParaAluno(Long alunoId) {
        if (!alunoRepository.existsById(alunoId)) {
            throw new EntityNotFoundException("Aluno não encontrado com ID: " + alunoId);
        }
        List<Tarefa> tarefas = tarefaRepository.findByAlunoIdOrderByDataDesignacaoDesc(alunoId);
        return tarefas.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TarefaDTO buscarDetalhesTarefa(Long tarefaId, Long alunoId) {
        Tarefa tarefa = findTarefaByIdAndAluno(tarefaId, alunoId);
        return convertToDTO(tarefa);
    }

    @Override
    @Transactional
    public TarefaDTO marcarTarefaComoVisualizada(Long tarefaId, Long alunoId) {
        Tarefa tarefa = findTarefaByIdAndAluno(tarefaId, alunoId);
        if ("PENDENTE".equals(tarefa.getStatus())) {
            tarefa.setStatus("VISUALIZADA");
            tarefaRepository.save(tarefa);
        }
        return convertToDTO(tarefa);
    }

    @Override
    @Transactional
    public TarefaDTO submeterRespostaTarefa(Long tarefaId, Long alunoId, String respostaTexto, MultipartFile arquivoResposta) throws IOException {
        Tarefa tarefa = findTarefaByIdAndAluno(tarefaId, alunoId);
        if (!("PENDENTE".equals(tarefa.getStatus()) || "VISUALIZADA".equals(tarefa.getStatus()))) {
            throw new IllegalStateException("Não é possível submeter resposta para uma tarefa com status: " + tarefa.getStatus());
        }
        tarefa.setRespostaAlunoTexto(respostaTexto);

        // Lida com o upload do arquivo de resposta
        if (arquivoResposta != null && !arquivoResposta.isEmpty()) {
            // Deleta arquivo de resposta antigo, se existir
            deleteArquivoFisico(tarefa.getRespostaAlunoArquivo());

            // Salva novo arquivo
            String originalFilename = arquivoResposta.getOriginalFilename() != null ? arquivoResposta.getOriginalFilename() : "resposta";
            String fileExtension = "";
            int lastDot = originalFilename.lastIndexOf(".");
            if (lastDot > 0) {
                fileExtension = originalFilename.substring(lastDot);
            }
            String storedFilename = "aluno_" + alunoId + "_tarefa_" + tarefaId + "_" + UUID.randomUUID().toString() + fileExtension;
            Path targetLocation = this.respostaStorageLocation.resolve(storedFilename);
            Files.copy(arquivoResposta.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            tarefa.setRespostaAlunoArquivo(targetLocation.toString());
        } else {
             // Se não veio arquivo novo, remove o antigo (caso exista)
             deleteArquivoFisico(tarefa.getRespostaAlunoArquivo());
             tarefa.setRespostaAlunoArquivo(null);
        }

        tarefa.setStatus("ENTREGUE");
        tarefa.setDataEntrega(LocalDateTime.now());

        Tarefa savedTarefa = tarefaRepository.save(tarefa);
        return convertToDTO(savedTarefa);
    }

    // --- Métodos Auxiliares ---

    private Tarefa findTarefaByIdAndAluno(Long tarefaId, Long alunoId) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com ID: " + tarefaId));
        if (!tarefa.getAluno().getId().equals(alunoId)) {
            throw new AccessDeniedException("Aluno não tem permissão para acessar esta tarefa.");
        }
        return tarefa;
    }

    private void deleteArquivoFisico(String caminhoArquivo) throws IOException {
        if (caminhoArquivo != null && !caminhoArquivo.isBlank()) {
            Path path = Paths.get(caminhoArquivo);
            if (Files.exists(path)) {
                Files.delete(path);
            }
        }
    }

    private TarefaDTO convertToDTO(Tarefa tarefa) {
        TarefaDTO dto = new TarefaDTO();
        BeanUtils.copyProperties(tarefa, dto);

        if (tarefa.getAtividade() != null) {
            Atividade atividade = tarefa.getAtividade();
            dto.setAtividadeId(atividade.getId());
            dto.setAtividadeTitulo(atividade.getTitulo());
            dto.setAtividadeDescricao(atividade.getDescricao());
            dto.setAtividadeTipoConteudo(atividade.getTipoConteudo());
            dto.setAtividadeNomeArquivoOriginal(atividade.getNomeArquivoOriginal()); // Para link de download da atividade original
        }

        if (tarefa.getProfessorDesignador() != null) {
            Professor professor = tarefa.getProfessorDesignador();
            dto.setProfessorDesignadorId(professor.getId());
            dto.setProfessorDesignadorNome(professor.getNomeCompleto());
        }

        // Não incluir caminho do arquivo de resposta no DTO
        dto.setRespostaAlunoArquivo(null);

        return dto;
    }
}

