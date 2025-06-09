package br.com.plataformaeducacional.controller;

import br.com.plataformaeducacional.dto.DesignacaoAtividadeDTO;
import br.com.plataformaeducacional.entity.User;
import br.com.plataformaeducacional.service.AlunoAtividadeService;
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
@RequestMapping("/api/aluno/atividades")
@RequiredArgsConstructor
public class AlunoAtividadeController {
    private final AlunoAtividadeService alunoAtividadeService;

    @GetMapping("/designadas")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<List<DesignacaoAtividadeDTO>> listarMinhasAtividadesDesignadas(@AuthenticationPrincipal User user) {
        Long alunoId = user.getId();
        return ResponseEntity.ok(alunoAtividadeService.listarAtividadesDesignadasParaAluno(alunoId));
    }

    @GetMapping("/designadas/{designacaoId}")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<DesignacaoAtividadeDTO> buscarDetalhesAtividadeDesignada(@PathVariable Long designacaoId, @AuthenticationPrincipal User user) {
        Long alunoId = user.getId();
        DesignacaoAtividadeDTO dto = alunoAtividadeService.buscarDetalhesAtividadeDesignada(designacaoId, alunoId);
        alunoAtividadeService.marcarAtividadeComoVisualizada(designacaoId, alunoId);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/designadas/{designacaoId}/visualizar")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<DesignacaoAtividadeDTO> marcarComoVisualizada(@PathVariable Long designacaoId, @AuthenticationPrincipal User user) {
        Long alunoId = user.getId();
        return ResponseEntity.ok(alunoAtividadeService.marcarAtividadeComoVisualizada(designacaoId, alunoId));
    }

    @PostMapping(value = "/designadas/{designacaoId}/responder", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<DesignacaoAtividadeDTO> submeterResposta(@PathVariable Long designacaoId, @RequestPart(value = "respostaTexto", required = false) String respostaTexto, @RequestPart(value = "arquivoResposta", required = false) MultipartFile arquivoResposta, @AuthenticationPrincipal User user) throws IOException {
        Long alunoId = user.getId();
        return ResponseEntity.ok(alunoAtividadeService.submeterRespostaAtividade(designacaoId, alunoId, respostaTexto, arquivoResposta));
    }
}