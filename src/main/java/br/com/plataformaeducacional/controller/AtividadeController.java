package br.com.plataformaeducacional.controller;

import br.com.plataformaeducacional.dto.AtividadeDTO;
import br.com.plataformaeducacional.entity.Atividade;
import br.com.plataformaeducacional.entity.User;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/atividades") // Endpoint base para atividades (gerenciadas por professores)
@RequiredArgsConstructor
public class AtividadeController {

    private final AtividadeService atividadeService;
    // Injetar a implementação para acessar métodos auxiliares se necessário (ex: findAtividadeByIdAndProfessor)
    private final AtividadeServiceImpl atividadeServiceImpl;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AtividadeDTO> criarAtividade(
            @RequestPart("atividade") @Valid AtividadeDTO atividadeDTO,
            @RequestPart(value = "arquivo", required = false) MultipartFile arquivo,
            @AuthenticationPrincipal User user) throws IOException {

        // Assume que o UserDetails (User) tem o ID do usuário logado.
        // Precisamos garantir que o usuário logado é um professor.
        // A verificação de role já deve ter sido feita pelo SecurityConfig.
        // O ID do professor é o mesmo ID do usuário logado.
        Long professorId = user.getId();

        AtividadeDTO novaAtividade = atividadeService.criarAtividade(atividadeDTO, arquivo, professorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaAtividade);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtividadeDTO> buscarAtividadePorId(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        Long professorId = user.getId();
        AtividadeDTO atividadeDTO = atividadeService.buscarAtividadePorId(id, professorId);
        return ResponseEntity.ok(atividadeDTO);
    }

    @GetMapping("/professor/me")
    public ResponseEntity<List<AtividadeDTO>> listarMinhasAtividades(
            @AuthenticationPrincipal User user) {
        Long professorId = user.getId();
        List<AtividadeDTO> atividades = atividadeService.listarAtividadesPorProfessor(professorId);
        return ResponseEntity.ok(atividades);
    }

    // Endpoint para Admin listar atividades de um professor específico (opcional)
    // @GetMapping("/professor/{professorId}")
    // @PreAuthorize("hasRole(\'ADMIN\')")
    // public ResponseEntity<List<AtividadeDTO>> listarAtividadesPorProfessorAdmin(@PathVariable Long professorId) { ... }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AtividadeDTO> atualizarAtividade(
            @PathVariable Long id,
            @RequestPart("atividade") @Valid AtividadeDTO atividadeDTO,
            @RequestPart(value = "arquivo", required = false) MultipartFile arquivo,
            @AuthenticationPrincipal User user) throws IOException {
        Long professorId = user.getId();
        AtividadeDTO atividadeAtualizada = atividadeService.atualizarAtividade(id, atividadeDTO, arquivo, professorId);
        return ResponseEntity.ok(atividadeAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAtividade(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) throws IOException {
        Long professorId = user.getId();
        atividadeService.deletarAtividade(id, professorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> baixarArquivoAtividade(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) throws IOException {
        Long professorId = user.getId();
        // Reutiliza a lógica de busca e verificação de permissão
        Atividade atividade = atividadeServiceImpl.findAtividadeByIdAndProfessor(id, professorId);

        if (atividade.getCaminhoArquivo() == null || atividade.getCaminhoArquivo().isBlank()) {
            return ResponseEntity.notFound().build();
        }

        Path filePath = Paths.get(atividade.getCaminhoArquivo());
        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erro ao ler o arquivo.", e);
        }

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Não foi possível ler o arquivo: " + atividade.getNomeArquivoOriginal());
        }

        String contentType = atividade.getTipoMimeArquivo() != null ? atividade.getTipoMimeArquivo() : "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + atividade.getNomeArquivoOriginal() + "\"")
                .body(resource);
    }
}

