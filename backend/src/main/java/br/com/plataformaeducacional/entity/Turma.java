package br.com.plataformaeducacional.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "turmas")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Nome da turma é obrigatório")
    @Size(max = 100)
    @Column(nullable = false)
    private String nome; // Ex: "Maternal II A", "3º Ano B"

    @NotNull(message = "Ano letivo é obrigatório")
    @Column(name = "ano_letivo", nullable = false)
    private Integer anoLetivo; // Ex: 2025

    @NotBlank(message = "Turno é obrigatório")
    @Size(max = 50)
    @Column(nullable = false)
    private String turno; // Ex: MANHA, TARDE, INTEGRAL

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escola_id", nullable = false, referencedColumnName = "id")
    @ToString.Exclude
    private Escola escola;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false, referencedColumnName = "id")
    private Professor professor;

    @OneToMany(mappedBy = "turma", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<MatriculaAluno> matriculas;

    public Turma(String nome, Integer anoLetivo, String turno, Escola escola) {
        this.nome = nome;
        this.anoLetivo = anoLetivo;
        this.turno = turno;
        this.escola = escola;
    }
} 