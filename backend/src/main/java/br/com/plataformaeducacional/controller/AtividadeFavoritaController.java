package br.com.plataformaeducacional.controller;

import br.com.plataformaeducacional.dto.AtividadeFavoritaDTO;
import br.com.plataformaeducacional.service.AtividadeFavoritaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/atividades/favoritas")
@RequiredArgsConstructor
public class AtividadeFavoritaController {
    private final AtividadeFavoritaService favoritaService;

    @PostMapping
    public ResponseEntity<AtividadeFavoritaDTO> salvarFavorita(@RequestParam Long professorId, @RequestParam Long atividadeCompartilhadaId) {
        return ResponseEntity.ok(favoritaService.salvarFavorita(professorId, atividadeCompartilhadaId));
    }

    @DeleteMapping
    public ResponseEntity<Void> removerFavorita(@RequestParam Long professorId, @RequestParam Long atividadeCompartilhadaId) {
        favoritaService.removerFavorita(professorId, atividadeCompartilhadaId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/professor/{professorId}")
    public ResponseEntity<List<AtividadeFavoritaDTO>> listarFavoritasPorProfessor(@PathVariable Long professorId) {
        return ResponseEntity.ok(favoritaService.listarFavoritasPorProfessor(professorId));
    }
} 