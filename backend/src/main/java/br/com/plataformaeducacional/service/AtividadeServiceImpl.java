package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.dto.AtividadeDTO;
import br.com.plataformaeducacional.entity.Atividade;
import br.com.plataformaeducacional.entity.Tarefa;
import br.com.plataformaeducacional.entity.MatriculaAluno;
import br.com.plataformaeducacional.entity.Professor;
import br.com.plataformaeducacional.entity.Turma;
import br.com.plataformaeducacional.repository.AtividadeRepository;
import br.com.plataformaeducacional.repository.TarefaRepository;
import br.com.plataformaeducacional.repository.MatriculaAlunoRepository;
import br.com.plataformaeducacional.repository.ProfessorRepository;
import br.com.plataformaeducacional.repository.TurmaRepository;
import br.com.plataformaeducacional.repository.EscolaRepository;
import br.com.plataformaeducacional.entity.Escola;
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

import br.com.plataformaeducacional.enums.Role;
import br.com.plataformaeducacional.entity.LotacaoProfessor;

@Service
@RequiredArgsConstructor
public class AtividadeServiceImpl implements AtividadeService {
    private final AtividadeRepository atividadeRepository;
    private final ProfessorRepository professorRepository;
    private final TurmaRepository turmaRepository;
    private final MatriculaAlunoRepository matriculaAlunoRepository;
    private final TarefaRepository tarefaRepository;
    private final EscolaRepository escolaRepository;

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

        // Se não for ADMIN, força a escola do professor
        if (atividadeDTO.getEscolaId() == null && professor.getLotacoes() != null && !professor.getLotacoes().isEmpty()) {
            LotacaoProfessor lotacao = professor.getLotacoes().iterator().next();
            atividade.setEscola(lotacao.getEscola());
        } else if (atividadeDTO.getEscolaId() != null) {
            Escola novaEscola = escolaRepository.findById(atividadeDTO.getEscolaId())
                .orElseThrow(() -> new EntityNotFoundException("Escola não encontrada"));
            atividade.setEscola(novaEscola);
        }

        // Salva o texto corretamente
        atividade.setConteudoTexto(atividadeDTO.getConteudoTexto());

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
    public AtividadeDTO buscarAtividadePorId(Long id, Long professorId, Role role) {
        Atividade atividade;
        if (role == Role.ADMIN) {
            atividade = findAtividadeById(id);
        } else {
            atividade = findAtividadeByIdAndProfessor(id, professorId);
        }
        return convertToDTO(atividade);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AtividadeDTO> listarAtividadesPorProfessor(Long professorId) {
        if (!professorRepository.existsById(professorId)) {
            throw new EntityNotFoundException("Professor não encontrado com ID: " + professorId);
        }
        List<Atividade> atividades = atividadeRepository.findByProfessorCriadorId(professorId);
        return atividades.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AtividadeDTO atualizarAtividade(Long id, AtividadeDTO atividadeDTO, MultipartFile arquivo, Long professorId, Role role) throws IOException {
        Atividade atividadeExistente;
        if (role == Role.ADMIN) {
            atividadeExistente = findAtividadeById(id);
        } else {
            atividadeExistente = findAtividadeByIdAndProfessor(id, professorId);
        }
        // Atualiza campos básicos, exceto os relacionados ao arquivo e criador
        BeanUtils.copyProperties(atividadeDTO, atividadeExistente, "id", "professorCriadorId", "professorCriadorNome", "createdAt", "updatedAt", "caminhoArquivo", "nomeArquivoOriginal", "tipoMimeArquivo", "tamanhoArquivo", "professorCriador", "escola");
        // Permitir alterar professor e escola apenas se for ADMIN
        if (role == Role.ADMIN) {
            if (atividadeDTO.getProfessorId() != null) {
                Professor novoProfessor = professorRepository.findById(atividadeDTO.getProfessorId())
                    .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Professor não encontrado"));
                atividadeExistente.setProfessorCriador(novoProfessor);
            }
            if (atividadeDTO.getEscolaId() != null) {
                Escola novaEscola = escolaRepository.findById(atividadeDTO.getEscolaId())
                    .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Escola não encontrada"));
                atividadeExistente.setEscola(novaEscola);
            }
        }
        // Se não for ADMIN, força a escola do professor
        if (role != Role.ADMIN && atividadeExistente.getProfessorCriador() != null && atividadeExistente.getProfessorCriador().getLotacoes() != null && !atividadeExistente.getProfessorCriador().getLotacoes().isEmpty()) {
            LotacaoProfessor lotacao = atividadeExistente.getProfessorCriador().getLotacoes().iterator().next();
            atividadeExistente.setEscola(lotacao.getEscola());
        }
        // Salva o texto corretamente
        atividadeExistente.setConteudoTexto(atividadeDTO.getConteudoTexto());
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
            boolean jaDesignada = tarefaRepository.existsByAtividadeIdAndAlunoId(atividadeId, matricula.getAluno().getId());
            if (!jaDesignada) {
                Tarefa novaTarefa = new Tarefa(
                    atividade,
                    matricula.getAluno(),
                    atividade.getProfessorCriador()
                );
                tarefaRepository.save(novaTarefa);
            }
        }
    }

    @Override
    public List<AtividadeDTO> listarAtividadesPorTurma(Long turmaId) {
        // Buscar todos os alunos da turma
        List<MatriculaAluno> matriculas = matriculaAlunoRepository.findByTurmaId(turmaId);
        if (matriculas.isEmpty()) {
            return List.of();
        }
        List<Long> alunoIds = matriculas.stream()
            .map(m -> m.getAluno().getId())
            .toList();

        // Buscar todas as designações de atividades para esses alunos
        List<Tarefa> tarefas = tarefaRepository.findAll().stream()
            .filter(t -> alunoIds.contains(t.getAluno().getId()))
            .toList();

        // Mapear para DTOs de atividade, removendo duplicatas
        return tarefas.stream()
            .map(tarefa -> convertToDTO(tarefa.getAtividade()))
            .distinct()
            .toList();
    }

    // Método auxiliar para buscar e verificar permissão
    public Atividade findAtividadeByIdAndProfessor(Long atividadeId, Long professorId) {
        Atividade atividade = atividadeRepository.findById(atividadeId)
                .orElseThrow(() -> new EntityNotFoundException("Atividade não encontrada com ID: " + atividadeId));

        if (!atividade.getProfessorCriador().getId().equals(professorId)) {
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
            dto.setProfessorCriadorId(atividade.getProfessorCriador().getId());
            dto.setProfessorCriadorNome(atividade.getProfessorCriador().getNomeCompleto());
        }
        return dto;
    }
}

