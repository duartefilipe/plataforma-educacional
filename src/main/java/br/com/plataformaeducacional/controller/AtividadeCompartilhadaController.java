package br.com.plataformaeducacional.controller;

import br.com.plataformaeducacional.dto.AtividadeCompartilhadaDTO;
import br.com.plataformaeducacional.entity.User;
import br.com.plataformaeducacional.service.AtividadeCompartilhadaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/atividades/compartilhadas") // Endpoint base para atividades compartilhadas
@RequiredArgsConstructor
public class AtividadeCompartilhadaController {

    private final AtividadeCompartilhadaService atividadeCompartilhadaService;

    @PostMapping("/compartilhar/{atividadeId}")
    public ResponseEntity<AtividadeCompartilhadaDTO> compartilharAtividade(
            @PathVariable Long atividadeId,
            @RequestBody @Valid AtividadeCompartilhadaDTO compartilhamentoDTO, // Recebe os dados de categorização
            @AuthenticationPrincipal User user) {
        Long professorId = user.getId(); // Assume que o UserDetails tem o ID do usuário logado
        AtividadeCompartilhadaDTO dto = atividadeCompartilhadaService.compartilharAtividade(atividadeId, compartilhamentoDTO, professorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/remover/{atividadeId}")
    public ResponseEntity<Void> removerCompartilhamento(
            @PathVariable Long atividadeId,
            @AuthenticationPrincipal User user) {
        Long professorId = user.getId();
        atividadeCompartilhadaService.removerCompartilhamento(atividadeId, professorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtividadeCompartilhadaDTO> buscarCompartilhamentoPorId(@PathVariable Long id) {
        AtividadeCompartilhadaDTO dto = atividadeCompartilhadaService.buscarCompartilhamentoPorId(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/atividade/{atividadeId}")
    public ResponseEntity<AtividadeCompartilhadaDTO> buscarCompartilhamentoPorAtividadeId(@PathVariable Long atividadeId) {
        AtividadeCompartilhadaDTO dto = atividadeCompartilhadaService.buscarCompartilhamentoPorAtividadeId(atividadeId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Page<AtividadeCompartilhadaDTO>> buscarAtividadesCompartilhadas(
            @RequestParam(required = false) Map<String, String> filtros,
            @PageableDefault(size = 10, sort = "dataCompartilhamento") Pageable pageable) {
        // Os filtros são passados como query parameters, ex: ?disciplina=Matemática&anoEscolar=9º Ano
        Page<AtividadeCompartilhadaDTO> page = atividadeCompartilhadaService.buscarAtividadesCompartilhadas(filtros, pageable);
        return ResponseEntity.ok(page);
    }
}

