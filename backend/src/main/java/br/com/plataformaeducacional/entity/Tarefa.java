package br.com.plataformaeducacional.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "designacoes_atividades", uniqueConstraints = {
    // Um aluno só pode ter a mesma atividade designada uma vez
    @UniqueConstraint(columnNames = {"atividade_id", "aluno_user_id"})
})
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atividade_id", nullable = false)
    @NotNull
    @ToString.Exclude
    private Atividade atividade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_user_id", nullable = false)
    @NotNull
    @ToString.Exclude
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_designador_id", nullable = false)
    @NotNull
    @ToString.Exclude
    private Professor professorDesignador;

    @CreationTimestamp
    @Column(name = "data_designacao", nullable = false, updatable = false)
    private LocalDateTime dataDesignacao;

    @Size(max = 50)
    @Column(nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'PENDENTE'")
    private String status = "PENDENTE"; // PENDENTE, VISUALIZADA, ENTREGUE, AVALIADA

    @Column(name = "data_entrega")
    private LocalDateTime dataEntrega;

    @Column(name = "data_avaliacao")
    private LocalDateTime dataAvaliacao;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "10.0", inclusive = true) // Exemplo, pode variar
    @Column(precision = 4, scale = 2)
    private BigDecimal nota;

    @Column(name = "observacoes_professor", columnDefinition = "TEXT")
    private String observacoesProfessor;

    @Column(name = "resposta_aluno_texto", columnDefinition = "TEXT")
    private String respostaAlunoTexto;

    @Size(max = 512)
    @Column(name = "resposta_aluno_arquivo")
    private String respostaAlunoArquivo; // Caminho para o arquivo de resposta do aluno

    public Tarefa(Atividade atividade, Aluno aluno, Professor professorDesignador) {
        this.atividade = atividade;
        this.aluno = aluno;
        this.professorDesignador = professorDesignador;
        this.status = "PENDENTE";
    }
}

