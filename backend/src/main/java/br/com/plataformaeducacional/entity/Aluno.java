package br.com.plataformaeducacional.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "alunos")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Aluno {

    @Id
    @EqualsAndHashCode.Include
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Maps the userId field to the User's ID
    @JoinColumn(name = "user_id")
    @ToString.Exclude // Evitar recursão
    private User user;

    @PastOrPresent(message = "Data de nascimento deve ser no passado ou presente")
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Size(max = 255)
    @Column(name = "nome_responsavel")
    private String nomeResponsavel;

    @Size(max = 255)
    @Column(name = "contato_responsavel")
    private String contatoResponsavel; // Email ou telefone

    // Relacionamentos
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude // Evitar recursão no toString
    private Set<MatriculaAluno> matriculas = new HashSet<>();

    // Relacionamento com Atividades Designadas (será mapeado posteriormente)
    // @OneToMany(mappedBy = "aluno")
    // private Set<DesignacaoAtividade> atividadesDesignadas;

    public Aluno(User user) {
        this.user = user;
    }

    // Métodos utilitários para gerenciar matrículas (opcional, mas útil)
    public void addMatricula(MatriculaAluno matricula) {
        matriculas.add(matricula);
        matricula.setAluno(this);
    }

    public void removeMatricula(MatriculaAluno matricula) {
        matriculas.remove(matricula);
        matricula.setAluno(null);
    }
}

