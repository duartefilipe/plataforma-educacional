package br.com.plataformaeducacional.controller;

import br.com.plataformaeducacional.dto.EscolaDTO;
import br.com.plataformaeducacional.service.EscolaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/escolas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class EscolaController {

    private static final Logger logger = LoggerFactory.getLogger(EscolaController.class);

    private final EscolaService escolaService;

    @PostMapping
    public ResponseEntity<EscolaDTO> criarEscola(@Valid @RequestBody EscolaDTO escolaDTO) {
        logger.info("Recebido DTO para criar escola: {}", escolaDTO);
        return new ResponseEntity<>(escolaService.criarEscola(escolaDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EscolaDTO>> listarEscolas() {
        return ResponseEntity.ok(escolaService.listarEscolas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EscolaDTO> buscarEscolaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(escolaService.buscarEscolaPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EscolaDTO> atualizarEscola(@PathVariable Long id, @Valid @RequestBody EscolaDTO escolaDTO) {
        return ResponseEntity.ok(escolaService.atualizarEscola(id, escolaDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEscola(@PathVariable Long id) {
        escolaService.deletarEscola(id);
        return ResponseEntity.noContent().build();
    }
} 