package br.com.plataformaeducacional.controller;

import br.com.plataformaeducacional.dto.TurmaDTO;
import br.com.plataformaeducacional.service.TurmaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turmas")
@RequiredArgsConstructor
public class TurmaController {

    private final TurmaService turmaService;

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
} 