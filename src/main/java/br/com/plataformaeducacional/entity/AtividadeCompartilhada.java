package br.com.plataformaeducacional.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "atividades_compartilhadas")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AtividadeCompartilhada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atividade_id", nullable = false, unique = true)
    @NotNull
    @ToString.Exclude
    private Atividade atividade;

    @CreationTimestamp
    @Column(name = "data_compartilhamento", nullable = false, updatable = false)
    private LocalDateTime dataCompartilhamento;

    @Column(name = "idade_alvo_min")
    private Integer idadeAlvoMin;

    @Column(name = "idade_alvo_max")
    private Integer idadeAlvoMax;

    @Size(max = 50)
    @Column(name = "ano_escolar")
    private String anoEscolar; // Ex: "Infantil V", "1º Ano EF", "3º Ano EM"

    @Size(max = 100)
    @Column(name = "tipo_atividade")
    private String tipoAtividade; // Ex: "Avaliação", "Exercício Fixação"

    @Size(max = 100)
    @Column(name = "disciplina")
    private String disciplina;

    @Column(columnDefinition = "TEXT")
    private String tags; // Para filtros adicionais

    public AtividadeCompartilhada(Atividade atividade, Integer idadeAlvoMin, Integer idadeAlvoMax, String anoEscolar, String tipoAtividade, String disciplina, String tags) {
        this.atividade = atividade;
        this.idadeAlvoMin = idadeAlvoMin;
        this.idadeAlvoMax = idadeAlvoMax;
        this.anoEscolar = anoEscolar;
        this.tipoAtividade = tipoAtividade;
        this.disciplina = disciplina;
        this.tags = tags;
    }
}

