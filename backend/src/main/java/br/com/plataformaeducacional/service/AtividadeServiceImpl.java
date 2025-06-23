package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.dto.AtividadeDTO;
import br.com.plataformaeducacional.entity.Atividade;
import br.com.plataformaeducacional.entity.DesignacaoAtividade;
import br.com.plataformaeducacional.entity.MatriculaAluno;
import br.com.plataformaeducacional.entity.Professor;
import br.com.plataformaeducacional.entity.Turma;
import br.com.plataformaeducacional.repository.AtividadeRepository;
import br.com.plataformaeducacional.repository.DesignacaoAtividadeRepository;
import br.com.plataformaeducacional.repository.MatriculaAlunoRepository;
import br.com.plataformaeducacional.repository.ProfessorRepository;
import br.com.plataformaeducacional.repository.TurmaRepository;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AtividadeServiceImpl implements AtividadeService {
    private final AtividadeRepository atividadeRepository;
    private final ProfessorRepository professorRepository;
    private final TurmaRepository turmaRepository;
    private final MatriculaAlunoRepository matriculaAlunoRepository;
    private final DesignacaoAtividadeRepository designacaoAtividadeRepository;

    private final Path fileStorageLocation = Paths.get("uploads/atividades").toAbsolutePath().normalize();

    {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Não foi possível criar o diretório para armazenar os arquivos.", ex);
        }
    }


    @Override
    @Transactional
    public AtividadeDTO criarAtividade(AtividadeDTO atividadeDTO, MultipartFile arquivo, Long professorId) throws IOException {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado com ID: " + professorId));

        Atividade atividade = new Atividade();
        BeanUtils.copyProperties(atividadeDTO, atividade, "id", "professorCriadorId", "professorCriadorNome", "createdAt", "updatedAt", "caminhoArquivo", "nomeArquivoOriginal", "tipoMimeArquivo", "tamanhoArquivo");
        atividade.setProfessorCriador(professor);

        if (arquivo != null && !arquivo.isEmpty()) {
            if (!"ARQUIVO_UPLOAD".equalsIgnoreCase(atividade.getTipoConteudo())) {
                throw new IllegalArgumentException("Tipo de conteúdo deve ser ARQUIVO_UPLOAD quando um arquivo é fornecido.");
            }
            String originalFilename = arquivo.getOriginalFilename() != null ? arquivo.getOriginalFilename() : "arquivo";
            String fileExtension = "";
            int lastDot = originalFilename.lastIndexOf(".");
            if (lastDot > 0) {
                fileExtension = originalFilename.substring(lastDot);
            }
            String storedFilename = UUID.randomUUID().toString() + fileExtension;
            Path targetLocation = this.fileStorageLocation.resolve(storedFilename);
            Files.copy(arquivo.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            atividade.setCaminhoArquivo(targetLocation.toString());
            atividade.setNomeArquivoOriginal(originalFilename);
            atividade.setTipoMimeArquivo(arquivo.getContentType());
            atividade.setTamanhoArquivo(arquivo.getSize());
            atividade.setConteudoTexto(null); // Garante que não haja texto se for upload
        } else {
            if (!"TEXTO".equalsIgnoreCase(atividade.getTipoConteudo())) {
                throw new IllegalArgumentException("Tipo de conteúdo deve ser TEXTO quando nenhum arquivo é fornecido.");
            }
            atividade.setCaminhoArquivo(null);
            atividade.setNomeArquivoOriginal(null);
            atividade.setTipoMimeArquivo(null);
            atividade.setTamanhoArquivo(null);
        }

        Atividade savedAtividade = atividadeRepository.save(atividade);
        return convertToDTO(savedAtividade);
    }

    @Override
    @Transactional(readOnly = true)
    public AtividadeDTO buscarAtividadePorId(Long id, Long professorId) {
        Atividade atividade = findAtividadeByIdAndProfessor(id, professorId);
        return convertToDTO(atividade);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AtividadeDTO> listarAtividadesPorProfessor(Long professorId) {
        if (!professorRepository.existsById(professorId)) {
            throw new EntityNotFoundException("Professor não encontrado com ID: " + professorId);
        }
        List<Atividade> atividades = atividadeRepository.findByProfessorCriadorUserId(professorId);
        return atividades.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AtividadeDTO atualizarAtividade(Long id, AtividadeDTO atividadeDTO, MultipartFile arquivo, Long professorId) throws IOException {
        Atividade atividadeExistente = findAtividadeByIdAndProfessor(id, professorId);

        // Atualiza campos básicos, exceto os relacionados ao arquivo e criador
        BeanUtils.copyProperties(atividadeDTO, atividadeExistente, "id", "professorCriadorId", "professorCriadorNome", "createdAt", "updatedAt", "caminhoArquivo", "nomeArquivoOriginal", "tipoMimeArquivo", "tamanhoArquivo", "professorCriador");

        // Lógica para atualizar/substituir arquivo
        if (arquivo != null && !arquivo.isEmpty()) {
            if (!"ARQUIVO_UPLOAD".equalsIgnoreCase(atividadeExistente.getTipoConteudo())) {
                 throw new IllegalArgumentException("Não é possível adicionar arquivo a uma atividade do tipo TEXTO. Mude o tipo primeiro.");
            }
            // Deleta arquivo antigo se existir
            deleteArquivoFisico(atividadeExistente.getCaminhoArquivo());

            // Salva novo arquivo
            String originalFilename = arquivo.getOriginalFilename() != null ? arquivo.getOriginalFilename() : "arquivo";
            String fileExtension = "";
            int lastDot = originalFilename.lastIndexOf(".");
            if (lastDot > 0) {
                fileExtension = originalFilename.substring(lastDot);
            }
            String storedFilename = UUID.randomUUID().toString() + fileExtension;
            Path targetLocation = this.fileStorageLocation.resolve(storedFilename);
            Files.copy(arquivo.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            atividadeExistente.setCaminhoArquivo(targetLocation.toString());
            atividadeExistente.setNomeArquivoOriginal(originalFilename);
            atividadeExistente.setTipoMimeArquivo(arquivo.getContentType());
            atividadeExistente.setTamanhoArquivo(arquivo.getSize());
            atividadeExistente.setConteudoTexto(null); // Garante que não haja texto se for upload

        } else if ("TEXTO".equalsIgnoreCase(atividadeExistente.getTipoConteudo())) {
             // Se o tipo for TEXTO e não veio arquivo, remove infos de arquivo antigo (se houver)
             deleteArquivoFisico(atividadeExistente.getCaminhoArquivo());
             atividadeExistente.setCaminhoArquivo(null);
             atividadeExistente.setNomeArquivoOriginal(null);
             atividadeExistente.setTipoMimeArquivo(null);
             atividadeExistente.setTamanhoArquivo(null);
        }
        // Se for ARQUIVO_UPLOAD e não veio arquivo novo, mantém o antigo.

        Atividade savedAtividade = atividadeRepository.save(atividadeExistente);
        return convertToDTO(savedAtividade);
    }

    @Override
    @Transactional
    public void deletarAtividade(Long id, Long professorId) throws IOException {
        Atividade atividade = findAtividadeByIdAndProfessor(id, professorId);
        // Deleta o arquivo físico associado, se existir
        deleteArquivoFisico(atividade.getCaminhoArquivo());
        atividadeRepository.delete(atividade);
    }

    @Override
    @Transactional
    public void designarAtividadeParaTurma(Long atividadeId, Long turmaId, Long professorId) {
        Atividade atividade = findAtividadeByIdAndProfessor(atividadeId, professorId);

        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada com ID: " + turmaId));

        List<MatriculaAluno> matriculas = matriculaAlunoRepository.findByTurmaId(turmaId);
        if (matriculas.isEmpty()) {
            throw new EntityNotFoundException("Nenhum aluno encontrado nesta turma para designação.");
        }

        for (MatriculaAluno matricula : matriculas) {
            boolean jaDesignada = designacaoAtividadeRepository.existsByAtividadeIdAndAlunoUserId(atividadeId, matricula.getAluno().getUserId());
            if (!jaDesignada) {
                DesignacaoAtividade novaDesignacao = new DesignacaoAtividade(
                        atividade,
                        matricula.getAluno(),
                        atividade.getProfessorCriador()
                );
                designacaoAtividadeRepository.save(novaDesignacao);
            }
        }
    }

    // Método auxiliar para buscar e verificar permissão
    public Atividade findAtividadeByIdAndProfessor(Long atividadeId, Long professorId) {
        Atividade atividade = atividadeRepository.findById(atividadeId)
                .orElseThrow(() -> new EntityNotFoundException("Atividade não encontrada com ID: " + atividadeId));

        if (!atividade.getProfessorCriador().getUserId().equals(professorId)) {
            // Poderia verificar se é ADMIN também, dependendo da regra
            throw new AccessDeniedException("Professor não tem permissão para acessar/modificar esta atividade.");
        }
        return atividade;
    }

    // Método auxiliar para busca sem verificação de professor (uso administrativo)
    public Atividade findAtividadeById(Long atividadeId) {
        return atividadeRepository.findById(atividadeId)
                .orElseThrow(() -> new EntityNotFoundException("Atividade não encontrada com ID: " + atividadeId));
    }

    // Método auxiliar para deletar arquivo físico
    private void deleteArquivoFisico(String caminhoArquivo) throws IOException {
        if (caminhoArquivo != null && !caminhoArquivo.isBlank()) {
            Path path = Paths.get(caminhoArquivo);
            if (Files.exists(path)) {
                Files.delete(path);
            }
        }
    }

    // Método auxiliar para converter Entidade para DTO
    private AtividadeDTO convertToDTO(Atividade atividade) {
        AtividadeDTO dto = new AtividadeDTO();
        BeanUtils.copyProperties(atividade, dto);
        if (atividade.getProfessorCriador() != null) {
            dto.setProfessorCriadorId(atividade.getProfessorCriador().getUserId());
            // Para evitar consulta extra, pegamos o nome do User associado ao Professor
            if (atividade.getProfessorCriador().getUser() != null) {
                 dto.setProfessorCriadorNome(atividade.getProfessorCriador().getUser().getNomeCompleto());
            } else {
                 // Fallback caso o User não esteja carregado (Lazy Loading)
                 // Considerar carregar explicitamente se necessário ou usar projeção
                 Professor p = professorRepository.findById(atividade.getProfessorCriador().getUserId()).orElse(null);
                 if (p != null && p.getUser() != null) {
                     dto.setProfessorCriadorNome(p.getUser().getNomeCompleto());
                 }
            }
        }
        return dto;
    }
}

