package br.com.plataformaeducacional.controller;

import br.com.plataformaeducacional.dto.AtividadeDTO;
import br.com.plataformaeducacional.entity.Atividade;
import br.com.plataformaeducacional.entity.User;
import br.com.plataformaeducacional.enums.Role;
import br.com.plataformaeducacional.service.AtividadeService;
import br.com.plataformaeducacional.service.AtividadeServiceImpl; // Import implementation for potential download logic
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/atividades")
@RequiredArgsConstructor
public class AtividadeController {
    private final AtividadeService atividadeService;
    private final AtividadeServiceImpl atividadeServiceImpl;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    public ResponseEntity<AtividadeDTO> criarAtividade(@RequestPart("atividade") @Valid AtividadeDTO atividadeDTO, @RequestPart(value = "arquivo", required = false) MultipartFile arquivo, @AuthenticationPrincipal User user) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(atividadeService.criarAtividade(atividadeDTO, arquivo, user.getId()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    public ResponseEntity<AtividadeDTO> buscarAtividadePorId(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(atividadeService.buscarAtividadePorId(id, user.getId()));
    }

    @GetMapping("/professor/me")
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    public ResponseEntity<List<AtividadeDTO>> listarMinhasAtividades(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(atividadeService.listarAtividadesPorProfessor(user.getId()));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    public ResponseEntity<AtividadeDTO> atualizarAtividade(@PathVariable Long id, @RequestPart("atividade") @Valid AtividadeDTO atividadeDTO, @RequestPart(value = "arquivo", required = false) MultipartFile arquivo, @AuthenticationPrincipal User user) throws IOException {
        return ResponseEntity.ok(atividadeService.atualizarAtividade(id, atividadeDTO, arquivo, user.getId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> deletarAtividade(@PathVariable Long id, @AuthenticationPrincipal User user) throws IOException {
        atividadeService.deletarAtividade(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    public ResponseEntity<Resource> baixarArquivoAtividade(@PathVariable Long id, @AuthenticationPrincipal User user) throws IOException {
        Atividade atividade = user.getRole() == Role.ADMIN ? atividadeServiceImpl.findAtividadeById(id) : atividadeServiceImpl.findAtividadeByIdAndProfessor(id, user.getId());

        if (atividade.getCaminhoArquivo() == null || atividade.getCaminhoArquivo().isBlank()) return ResponseEntity.notFound().build();

        Path filePath = Paths.get(atividade.getCaminhoArquivo());
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) throw new RuntimeException("Arquivo não pode ser lido.");

        String contentType = atividade.getTipoMimeArquivo() != null ? atividade.getTipoMimeArquivo() : "application/octet-stream";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + atividade.getNomeArquivoOriginal() + "\"")
                .body(resource);
    }
}