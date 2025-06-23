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
public class EscolaController {

    private static final Logger logger = LoggerFactory.getLogger(EscolaController.class);

    private final EscolaService escolaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EscolaDTO> createEscola(@RequestBody @Valid EscolaDTO escolaDTO) {
        logger.info("Recebido DTO para criar escola: {}", escolaDTO);
        return new ResponseEntity<>(escolaService.createEscola(escolaDTO), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EscolaDTO>> getAllEscolas() {
        return ResponseEntity.ok(escolaService.getAllEscolas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EscolaDTO> getEscolaById(@PathVariable Long id) {
        return escolaService.getEscolaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EscolaDTO> updateEscola(@PathVariable Long id, @RequestBody @Valid EscolaDTO escolaDTO) {
        return ResponseEntity.ok(escolaService.atualizarEscola(id, escolaDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEscola(@PathVariable Long id) {
        escolaService.deleteEscola(id);
        return ResponseEntity.noContent().build();
    }
} 