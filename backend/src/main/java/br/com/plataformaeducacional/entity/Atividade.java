package br.com.plataformaeducacional.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "atividades")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Atividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 255)
    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @NotBlank(message = "Tipo de conteúdo é obrigatório")
    @Size(max = 50)
    @Column(name = "tipo_conteudo", nullable = false)
    private String tipoConteudo; // Ex: TEXTO, ARQUIVO_UPLOAD

    @Column(name = "conteudo_texto", columnDefinition = "TEXT")
    private String conteudoTexto; // Nulo se for upload

    @Size(max = 512)
    @Column(name = "caminho_arquivo")
    private String caminhoArquivo; // Nulo se for texto. Caminho no sistema de arquivos ou storage.

    @Size(max = 255)
    @Column(name = "nome_arquivo_original")
    private String nomeArquivoOriginal;

    @Size(max = 100)
    @Column(name = "tipo_mime_arquivo")
    private String tipoMimeArquivo;

    @Column(name = "tamanho_arquivo")
    private Long tamanhoArquivo; // Em bytes

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_criador_id", nullable = false)
    @ToString.Exclude
    private Professor professorCriador;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relacionamentos (serão mapeados posteriormente)
    // @OneToOne(mappedBy = "atividade", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // private AtividadeCompartilhada atividadeCompartilhada;

    // @OneToMany(mappedBy = "atividade", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // private Set<DesignacaoAtividade> designacoes;

    public Atividade(String titulo, String descricao, String tipoConteudo, String conteudoTexto, String caminhoArquivo, String nomeArquivoOriginal, String tipoMimeArquivo, Long tamanhoArquivo, Professor professorCriador) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.tipoConteudo = tipoConteudo;
        this.conteudoTexto = conteudoTexto;
        this.caminhoArquivo = caminhoArquivo;
        this.nomeArquivoOriginal = nomeArquivoOriginal;
        this.tipoMimeArquivo = tipoMimeArquivo;
        this.tamanhoArquivo = tamanhoArquivo;
        this.professorCriador = professorCriador;
    }
}

