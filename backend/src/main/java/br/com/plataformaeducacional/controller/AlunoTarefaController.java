package br.com.plataformaeducacional.controller;

import br.com.plataformaeducacional.dto.TarefaDTO;
import br.com.plataformaeducacional.entity.User;
import br.com.plataformaeducacional.entity.Aluno;
import br.com.plataformaeducacional.entity.Atividade;
import br.com.plataformaeducacional.entity.Tarefa;
import br.com.plataformaeducacional.entity.Professor;
import br.com.plataformaeducacional.service.AlunoTarefaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.nio.file.Path;
import java.nio.file.Paths;
import jakarta.persistence.EntityNotFoundException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AlunoTarefaController {
    private final AlunoTarefaService alunoTarefaService;
    private final br.com.plataformaeducacional.repository.TarefaRepository tarefaRepository;

    @GetMapping
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<List<TarefaDTO>> listarMinhasTarefas(@AuthenticationPrincipal User user) {
        Long alunoId = user.getId();
        return ResponseEntity.ok(alunoTarefaService.listarTarefasParaAluno(alunoId));
    }

    @GetMapping("/{tarefaId}")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<TarefaDTO> buscarDetalhesTarefa(@PathVariable Long tarefaId, @AuthenticationPrincipal User user) {
        Long alunoId = user.getId();
        TarefaDTO dto = alunoTarefaService.buscarDetalhesTarefa(tarefaId, alunoId);
        alunoTarefaService.marcarTarefaComoVisualizada(tarefaId, alunoId);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{tarefaId}/visualizar")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<TarefaDTO> marcarComoVisualizada(@PathVariable Long tarefaId, @AuthenticationPrincipal User user) {
        Long alunoId = user.getId();
        return ResponseEntity.ok(alunoTarefaService.marcarTarefaComoVisualizada(tarefaId, alunoId));
    }

    @PostMapping(value = "/{tarefaId}/responder", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<TarefaDTO> submeterResposta(@PathVariable Long tarefaId, @RequestPart(value = "respostaTexto", required = false) String respostaTexto, @RequestPart(value = "arquivoResposta", required = false) MultipartFile arquivoResposta, @AuthenticationPrincipal User user) throws IOException {
        Long alunoId = user.getId();
        return ResponseEntity.ok(alunoTarefaService.submeterRespostaTarefa(tarefaId, alunoId, respostaTexto, arquivoResposta));
    }

    @GetMapping("/professor/tarefas")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<List<br.com.plataformaeducacional.dto.TarefaDTO>> listarTarefasDesignadas(@AuthenticationPrincipal br.com.plataformaeducacional.entity.User user) {
        // Buscar todas as tarefas onde o professor é o designador
        var tarefas = tarefaRepository.findByProfessorDesignadorUserId(user.getId());
        // Converter para DTO (ajuste conforme seu conversor)
        List<br.com.plataformaeducacional.dto.TarefaDTO> dtos = tarefas.stream().map(t -> {
            br.com.plataformaeducacional.dto.TarefaDTO dto = new br.com.plataformaeducacional.dto.TarefaDTO();
            dto.setId(t.getId());
            dto.setStatus(t.getStatus());
            dto.setDataDesignacao(t.getDataDesignacao());
            dto.setDataEntrega(t.getDataEntrega());
            dto.setAlunoNome(t.getAluno().getNomeCompleto());
            dto.setAtividadeTitulo(t.getAtividade().getTitulo());
            // Preencher turmaNome se possível
            if (t.getAluno() != null && t.getAluno().getMatriculas() != null && !t.getAluno().getMatriculas().isEmpty()) {
                var matricula = t.getAluno().getMatriculas().iterator().next();
                if (matricula.getTurma() != null) {
                    dto.setTurmaNome(matricula.getTurma().getNome());
                }
            }
            // Adicione campos de resposta do aluno
            dto.setRespostaAlunoTexto(t.getRespostaAlunoTexto());
            dto.setRespostaAlunoArquivo(t.getRespostaAlunoArquivo());
            return dto;
        }).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/professor/tarefas/turma/{turmaId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<List<br.com.plataformaeducacional.dto.TarefaDTO>> listarTarefasPorTurma(
            @PathVariable Long turmaId,
            @AuthenticationPrincipal br.com.plataformaeducacional.entity.User user) {
        
        // Buscar tarefas da turma específica onde o professor é o designador
        List<Tarefa> tarefas = tarefaRepository.findByProfessorDesignadorUserIdAndTurmaId(user.getId(), turmaId);
        
        List<br.com.plataformaeducacional.dto.TarefaDTO> dtos = tarefas.stream().map(t -> {
            br.com.plataformaeducacional.dto.TarefaDTO dto = new br.com.plataformaeducacional.dto.TarefaDTO();
            dto.setId(t.getId());
            dto.setStatus(t.getStatus());
            dto.setDataDesignacao(t.getDataDesignacao());
            dto.setDataEntrega(t.getDataEntrega());
            dto.setDataAvaliacao(t.getDataAvaliacao());
            dto.setNota(t.getNota());
            dto.setAlunoNome(t.getAluno().getNomeCompleto());
            dto.setAtividadeTitulo(t.getAtividade().getTitulo());
            dto.setAtividadeId(t.getAtividade().getId());
            dto.setProfessorDesignadorId(t.getProfessorDesignador().getId());
            dto.setProfessorDesignadorNome(t.getProfessorDesignador().getNomeCompleto());
            
            // Preencher turmaNome
            if (t.getAluno() != null && t.getAluno().getMatriculas() != null && !t.getAluno().getMatriculas().isEmpty()) {
                var matricula = t.getAluno().getMatriculas().iterator().next();
                if (matricula.getTurma() != null) {
                    dto.setTurmaNome(matricula.getTurma().getNome());
                }
            }
            
            // Adicione campos de resposta do aluno
            dto.setRespostaAlunoTexto(t.getRespostaAlunoTexto());
            dto.setRespostaAlunoArquivo(t.getRespostaAlunoArquivo());
            return dto;
        }).toList();
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/professor/tarefas/{tarefaId}/download-resposta")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<Resource> downloadRespostaAluno(@PathVariable Long tarefaId, @AuthenticationPrincipal br.com.plataformaeducacional.entity.User user) throws IOException {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
            .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada"));
        if (tarefa.getRespostaAlunoArquivo() == null) {
            return ResponseEntity.notFound().build();
        }
        Path filePath = Paths.get(tarefa.getRespostaAlunoArquivo());
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        String fileName = filePath.getFileName().toString();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .body(resource);
    }

    @PutMapping("/professor/tarefas/{tarefaId}/nota")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<Void> atualizarNotaTarefa(@PathVariable Long tarefaId, @RequestParam BigDecimal nota, @AuthenticationPrincipal br.com.plataformaeducacional.entity.User user) {
        if (nota == null || nota.compareTo(BigDecimal.ZERO) < 0 || nota.compareTo(new BigDecimal("10")) > 0) {
            return ResponseEntity.badRequest().build();
        }
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
            .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada"));
        tarefa.setNota(nota);
        tarefa.setDataAvaliacao(LocalDateTime.now());
        tarefaRepository.save(tarefa);
        return ResponseEntity.ok().build();
    }

    // Endpoint 1: Listar tarefas do aluno logado
    @GetMapping("/aluno/tarefas")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<List<TarefaDTO>> listarTarefasAluno(@AuthenticationPrincipal br.com.plataformaeducacional.entity.User user) {
        List<Tarefa> tarefas = tarefaRepository.findByAlunoIdOrderByDataDesignacaoDesc(user.getId());
        List<TarefaDTO> dtos = tarefas.stream().map(t -> {
            TarefaDTO dto = new TarefaDTO();
            dto.setId(t.getId());
            dto.setStatus(t.getStatus());
            dto.setDataDesignacao(t.getDataDesignacao());
            dto.setDataEntrega(t.getDataEntrega());
            dto.setDataAvaliacao(t.getDataAvaliacao());
            dto.setNota(t.getNota());
            dto.setAtividadeTitulo(t.getAtividade().getTitulo());
            dto.setAtividadeId(t.getAtividade().getId());
            dto.setRespostaAlunoTexto(t.getRespostaAlunoTexto());
            dto.setRespostaAlunoArquivo(t.getRespostaAlunoArquivo());
            return dto;
        }).toList();
        return ResponseEntity.ok(dtos);
    }

    // Endpoint 2: Enviar resposta (texto/arquivo) para uma tarefa
    @PostMapping(value = "/aluno/tarefas/{tarefaId}/responder", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<Void> responderTarefa(
            @PathVariable Long tarefaId,
            @RequestPart(value = "respostaTexto", required = false) String respostaTexto,
            @RequestPart(value = "respostaArquivo", required = false) MultipartFile respostaArquivo,
            @AuthenticationPrincipal br.com.plataformaeducacional.entity.User user) throws IOException {
        System.out.println("Recebido respostaTexto: " + respostaTexto);
        System.out.println("Recebido respostaArquivo: " + (respostaArquivo != null ? respostaArquivo.getOriginalFilename() : "null"));
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
            .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada"));
        if (!tarefa.getAluno().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }
        if (respostaTexto != null && !respostaTexto.isBlank()) {
            tarefa.setRespostaAlunoTexto(respostaTexto);
        }
        if (respostaArquivo != null && !respostaArquivo.isEmpty()) {
            // Salvar arquivo
            Path uploadDir = Paths.get("uploads/respostas-alunos").toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);
            String fileName = "tarefa_" + tarefaId + "_aluno_" + user.getId() + "_" + respostaArquivo.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(respostaArquivo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            tarefa.setRespostaAlunoArquivo(filePath.toString());
        }
        tarefa.setStatus("ENTREGUE");
        tarefa.setDataEntrega(LocalDateTime.now());
        tarefaRepository.save(tarefa);
        System.out.println("Salvo respostaAlunoTexto: " + tarefa.getRespostaAlunoTexto());
        return ResponseEntity.ok().build();
    }

    // Endpoint 3: Download do arquivo de resposta do aluno
    @GetMapping("/aluno/tarefas/{tarefaId}/download-resposta")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<Resource> downloadRespostaAlunoAluno(@PathVariable Long tarefaId, @AuthenticationPrincipal br.com.plataformaeducacional.entity.User user) throws IOException {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
            .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada"));
        if (!tarefa.getAluno().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }
        if (tarefa.getRespostaAlunoArquivo() == null) {
            return ResponseEntity.notFound().build();
        }
        Path filePath = Paths.get(tarefa.getRespostaAlunoArquivo());
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        String fileName = filePath.getFileName().toString();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .body(resource);
    }
}