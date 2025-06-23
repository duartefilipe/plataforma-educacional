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
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@PrimaryKeyJoinColumn(name = "user_id")
public class Aluno extends User {

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

    // Relacionamento com Tarefas (será mapeado posteriormente)
    // @OneToMany(mappedBy = "aluno")
    // private Set<Tarefa> tarefas;

    public Aluno(User user) {
        super();
        this.setId(user.getId());
        this.setNomeCompleto(user.getNomeCompleto());
        this.setEmail(user.getEmail());
        this.setSenha(user.getSenha());
        this.setRole(user.getRole());
        this.setAtivo(user.isAtivo());
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

