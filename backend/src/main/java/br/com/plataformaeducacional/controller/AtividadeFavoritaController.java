package br.com.plataformaeducacional.controller;

import br.com.plataformaeducacional.dto.AtividadeFavoritaDTO;
import br.com.plataformaeducacional.service.AtividadeFavoritaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import br.com.plataformaeducacional.entity.User;

@RestController
@RequestMapping("/api/atividades/favoritas")
@RequiredArgsConstructor
public class AtividadeFavoritaController {
    private final AtividadeFavoritaService favoritaService;

    @PostMapping
    public ResponseEntity<AtividadeFavoritaDTO> salvarFavorita(
        @RequestParam Long atividadeCompartilhadaId,
        @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(favoritaService.salvarFavorita(user.getId(), atividadeCompartilhadaId));
    }

    @DeleteMapping
    public ResponseEntity<Void> removerFavorita(
        @RequestParam Long atividadeCompartilhadaId,
        @AuthenticationPrincipal User user
    ) {
        favoritaService.removerFavorita(user.getId(), atividadeCompartilhadaId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/professor/{professorId}")
    public ResponseEntity<List<AtividadeFavoritaDTO>> listarFavoritasPorProfessor(@PathVariable Long professorId) {
        return ResponseEntity.ok(favoritaService.listarFavoritasPorProfessor(professorId));
    }
} 