package br.com.plataformaeducacional.controller;

import br.com.plataformaeducacional.dto.EscolaDTO;
import br.com.plataformaeducacional.dto.TurmaDTO;
import br.com.plataformaeducacional.service.TurmaService;
import br.com.plataformaeducacional.service.UserService;
import br.com.plataformaeducacional.service.AtividadeService;
import br.com.plataformaeducacional.dto.AtividadeDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@RestController
@RequestMapping("/api/turmas")
@RequiredArgsConstructor
public class TurmaController {

    private final TurmaService turmaService;
    private final UserService userService;
    private final AtividadeService atividadeService;

    @PostMapping
    public ResponseEntity<TurmaDTO> createTurma(@RequestBody TurmaDTO turmaDTO) {
        return ResponseEntity.ok(turmaService.createTurma(turmaDTO));
    }

    @GetMapping
    public ResponseEntity<List<TurmaDTO>> getAllTurmas(@RequestParam(required = false) Long escolaId) {
        return ResponseEntity.ok(turmaService.getAllTurmas(escolaId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TurmaDTO> updateTurma(@PathVariable Long id, @RequestBody @Valid TurmaDTO turmaDTO) {
        TurmaDTO updated = turmaService.updateTurma(id, turmaDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/me/escolas")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<List<EscolaDTO>> getEscolasDoProfessor(@AuthenticationPrincipal br.com.plataformaeducacional.entity.User user) {
        return ResponseEntity.ok(userService.getEscolasDoProfessor(user.getId()));
    }

    @GetMapping("/{turmaId}/atividades")
    public ResponseEntity<List<AtividadeDTO>> listarAtividadesPorTurma(@PathVariable Long turmaId) {
        return ResponseEntity.ok(atividadeService.listarAtividadesPorTurma(turmaId));
    }
} 