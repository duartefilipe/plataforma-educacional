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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class Professor {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

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
        this.user = user;
        this.disciplinas = disciplinas;
    }

    public Professor(User user) {
        this.user = user;
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
        return this.user != null ? this.user.getId() : null;
    }
}

