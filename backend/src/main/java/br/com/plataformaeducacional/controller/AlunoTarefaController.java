package br.com.plataformaeducacional.controller;

import br.com.plataformaeducacional.dto.TarefaDTO;
import br.com.plataformaeducacional.entity.User;
import br.com.plataformaeducacional.service.AlunoTarefaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            // Adicione outros campos necessários
            return dto;
        }).toList();
        return ResponseEntity.ok(dtos);
    }
}