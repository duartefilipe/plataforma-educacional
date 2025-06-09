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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/atividades/compartilhadas")
@RequiredArgsConstructor
public class AtividadeCompartilhadaController {
    private final AtividadeCompartilhadaService atividadeCompartilhadaService;

    @PostMapping("/compartilhar/{atividadeId}")
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    public ResponseEntity<AtividadeCompartilhadaDTO> compartilharAtividade(@PathVariable Long atividadeId, @RequestBody @Valid AtividadeCompartilhadaDTO compartilhamentoDTO, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(atividadeCompartilhadaService.compartilharAtividade(atividadeId, compartilhamentoDTO, user.getId()));
    }

    @DeleteMapping("/remover/{atividadeId}")
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> removerCompartilhamento(@PathVariable Long atividadeId, @AuthenticationPrincipal User user) {
        atividadeCompartilhadaService.removerCompartilhamento(atividadeId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AtividadeCompartilhadaDTO> buscarCompartilhamentoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(atividadeCompartilhadaService.buscarCompartilhamentoPorId(id));
    }

    @GetMapping("/atividade/{atividadeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AtividadeCompartilhadaDTO> buscarCompartilhamentoPorAtividadeId(@PathVariable Long atividadeId) {
        return ResponseEntity.ok(atividadeCompartilhadaService.buscarCompartilhamentoPorAtividadeId(atividadeId));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<AtividadeCompartilhadaDTO>> buscarAtividadesCompartilhadas(@RequestParam(required = false) Map<String, String> filtros, @PageableDefault(size = 10, sort = "dataCompartilhamento") Pageable pageable) {
        return ResponseEntity.ok(atividadeCompartilhadaService.buscarAtividadesCompartilhadas(filtros, pageable));
    }
}