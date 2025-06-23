package br.com.plataformaeducacional.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "matriculas_alunos", uniqueConstraints = {
    // Um aluno não pode estar matriculado na mesma escola, no mesmo turno e ano letivo mais de uma vez
    @UniqueConstraint(columnNames = {"aluno_user_id", "escola_id", "turno", "ano_letivo"})
})
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MatriculaAluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_user_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escola_id", nullable = false)
    private Escola escola;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turma_id") // Pode ser nulo se o aluno ainda não foi enturmado
    @ToString.Exclude
    private Turma turma;

    @NotBlank(message = "Turno é obrigatório")
    @Size(max = 50)
    @Column(nullable = false)
    private String turno; // Ex: MANHA, TARDE, INTEGRAL

    @NotNull(message = "Ano letivo é obrigatório")
    @Column(name = "ano_letivo", nullable = false)
    private Integer anoLetivo; // Ex: 2025

    public MatriculaAluno(Aluno aluno, Escola escola, String turno, Integer anoLetivo) {
        this.aluno = aluno;
        this.escola = escola;
        this.turno = turno;
        this.anoLetivo = anoLetivo;
    }
}

