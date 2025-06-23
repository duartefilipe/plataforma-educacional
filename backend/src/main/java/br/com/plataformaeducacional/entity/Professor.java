package br.com.plataformaeducacional.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "professores")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@PrimaryKeyJoinColumn(name = "user_id")
public class Professor extends User {

    @Column(columnDefinition = "TEXT")
    private String disciplinas;

    // Relacionamentos
    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude // Evitar recursão no toString
    private Set<LotacaoProfessor> lotacoes = new HashSet<>();

    // Relacionamentos com Atividades (serão mapeados posteriormente)
    // @OneToMany(mappedBy = "professorCriador")
    // private Set<Atividade> atividadesCriadas;

    // @OneToMany(mappedBy = "professorDesignador")
    // private Set<DesignacaoAtividade> atividadesDesignadas;

    public Professor(User user, String disciplinas) {
        super();
        this.setId(user.getId());
        this.setNomeCompleto(user.getNomeCompleto());
        this.setEmail(user.getEmail());
        this.setSenha(user.getSenha());
        this.setRole(user.getRole());
        this.setAtivo(user.isAtivo());
        this.disciplinas = disciplinas;
    }

    // Métodos utilitários para gerenciar lotações (opcional, mas útil)
    public void addLotacao(LotacaoProfessor lotacao) {
        lotacoes.add(lotacao);
        lotacao.setProfessor(this);
    }

    public void removeLotacao(LotacaoProfessor lotacao) {
        lotacoes.remove(lotacao);
        lotacao.setProfessor(null);
    }

    public Long getUserId() {
        return this.getId();
    }
}

