package br.com.plataformaeducacional.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AtividadeDTO {
    private Long id;
    @NotBlank(message = "Título é obrigatório")
    @Size(max = 255)
    private String titulo;
    private String descricao;
    @NotBlank(message = "Tipo de conteúdo é obrigatório")
    @Size(max = 50)
    private String tipoConteudo; // TEXTO, ARQUIVO_UPLOAD
    private String conteudoTexto;
    private String nomeArquivoOriginal;
    private String tipoMimeArquivo;
    private Long tamanhoArquivo;
    private Long professorCriadorId;
    private String professorCriadorNome; // Para exibição
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Não incluir caminhoArquivo diretamente no DTO por segurança/abstração
}

