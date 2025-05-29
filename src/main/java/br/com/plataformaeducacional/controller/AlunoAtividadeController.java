package br.com.plataformaeducacional.controller;

import br.com.plataformaeducacional.dto.DesignacaoAtividadeDTO;
import br.com.plataformaeducacional.entity.User;
import br.com.plataformaeducacional.service.AlunoAtividadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/aluno/atividades") // Endpoint base para atividades do aluno
@RequiredArgsConstructor
public class AlunoAtividadeController {

    private final AlunoAtividadeService alunoAtividadeService;

    @GetMapping("/designadas")
    public ResponseEntity<List<DesignacaoAtividadeDTO>> listarMinhasAtividadesDesignadas(
            @AuthenticationPrincipal User user) {
        // O ID do aluno é o mesmo ID do usuário logado.
        Long alunoId = user.getId();
        List<DesignacaoAtividadeDTO> atividades = alunoAtividadeService.listarAtividadesDesignadasParaAluno(alunoId);
        return ResponseEntity.ok(atividades);
    }

    @GetMapping("/designadas/{designacaoId}")
    public ResponseEntity<DesignacaoAtividadeDTO> buscarDetalhesAtividadeDesignada(
            @PathVariable Long designacaoId,
            @AuthenticationPrincipal User user) {
        Long alunoId = user.getId();
        DesignacaoAtividadeDTO dto = alunoAtividadeService.buscarDetalhesAtividadeDesignada(designacaoId, alunoId);
        // Ao buscar detalhes, podemos marcar como visualizada automaticamente
        alunoAtividadeService.marcarAtividadeComoVisualizada(designacaoId, alunoId);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/designadas/{designacaoId}/visualizar") // Usando PATCH para ação idempotente parcial
    public ResponseEntity<DesignacaoAtividadeDTO> marcarComoVisualizada(
            @PathVariable Long designacaoId,
            @AuthenticationPrincipal User user) {
        Long alunoId = user.getId();
        DesignacaoAtividadeDTO dto = alunoAtividadeService.marcarAtividadeComoVisualizada(designacaoId, alunoId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping(value = "/designadas/{designacaoId}/responder", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DesignacaoAtividadeDTO> submeterResposta(
            @PathVariable Long designacaoId,
            @RequestPart(value = "respostaTexto", required = false) String respostaTexto,
            @RequestPart(value = "arquivoResposta", required = false) MultipartFile arquivoResposta,
            @AuthenticationPrincipal User user) throws IOException {
        Long alunoId = user.getId();
        DesignacaoAtividadeDTO dto = alunoAtividadeService.submeterRespostaAtividade(designacaoId, alunoId, respostaTexto, arquivoResposta);
        return ResponseEntity.ok(dto);
    }

    // Endpoint para download do arquivo da atividade original (pode redirecionar ou chamar AtividadeController)
    // Ex: GET /api/aluno/atividades/designadas/{designacaoId}/download-original

    // Endpoint para download do arquivo de resposta do aluno (se necessário)
    // Ex: GET /api/aluno/atividades/designadas/{designacaoId}/download-resposta
}

