package br.com.plataformaeducacional.controller;

import br.com.plataformaeducacional.dto.AtividadeDTO;
import br.com.plataformaeducacional.dto.DesignacaoTurmaDTO;
import br.com.plataformaeducacional.entity.Atividade;
import br.com.plataformaeducacional.entity.User;
import br.com.plataformaeducacional.enums.Role;
import br.com.plataformaeducacional.service.AtividadeService;
import br.com.plataformaeducacional.service.AtividadeServiceImpl;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import java.util.HashMap;
import java.util.Map;

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
        return ResponseEntity.ok(
            user.getRole() == Role.ADMIN
                ? atividadeService.buscarAtividadePorId(id, null, user.getRole())
                : atividadeService.buscarAtividadePorId(id, user.getId(), user.getRole())
        );
    }

    @GetMapping("/professor/me")
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    public ResponseEntity<List<AtividadeDTO>> listarMinhasAtividades(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(atividadeService.listarAtividadesPorProfessor(user.getId()));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    public ResponseEntity<AtividadeDTO> atualizarAtividade(@PathVariable Long id, @RequestPart("atividade") @Valid AtividadeDTO atividadeDTO, @RequestPart(value = "arquivo", required = false) MultipartFile arquivo, @AuthenticationPrincipal User user) throws IOException {
        return ResponseEntity.ok(atividadeService.atualizarAtividade(id, atividadeDTO, arquivo, user.getId(), user.getRole()));
    }

    @PostMapping("/{id}/designar-turma")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<Void> designarAtividadeParaTurma(@PathVariable Long id, @RequestBody @Valid DesignacaoTurmaDTO designacaoDTO, @AuthenticationPrincipal User user) {
        atividadeServiceImpl.designarAtividadeParaTurma(id, designacaoDTO.getTurmaId(), user.getId());
        return ResponseEntity.ok().build();
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

    @PostMapping("/gerar-smart")
    public ResponseEntity<Map<String, String>> gerarAtividadeSmart(@RequestBody Map<String, String> params) {
        String ano = params.getOrDefault("ano", "");
        String idade = params.getOrDefault("idade", "");
        String disciplina = params.getOrDefault("disciplina", "");
        String tipo = params.getOrDefault("tipo", "");

        String prompt = String.format(
            "Gere um planejamento de aula para um dia completo para alunos do %s ano, com idade de %s, onde seja da disciplina de %s, que seja %s.",
            ano, idade, disciplina, tipo
        );

        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            return ResponseEntity.status(500).body(Map.of("erro", "Chave da API OpenAI não configurada"));
        }
        
        String url = "https://api.openai.com/v1/chat/completions";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("messages", new Object[] {
            new HashMap<String, String>() {{
                put("role", "user");
                put("content", prompt);
            }}
        });
        body.put("max_tokens", 800);
        body.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object choices = response.getBody().get("choices");
                if (choices instanceof java.util.List && !((java.util.List<?>) choices).isEmpty()) {
                    Object first = ((java.util.List<?>) choices).get(0);
                    if (first instanceof Map) {
                        Object message = ((Map<?, ?>) first).get("message");
                        if (message instanceof Map) {
                            Object content = ((Map<?, ?>) message).get("content");
                            if (content != null) {
                                Map<String, String> result = new HashMap<>();
                                result.put("conteudo", content.toString());
                                return ResponseEntity.ok(result);
                            }
                        }
                    }
                }
            }
            return ResponseEntity.status(500).body(Map.of("erro", "Não foi possível gerar o conteúdo."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro ao chamar o ChatGPT: " + e.getMessage()));
        }
    }
} 