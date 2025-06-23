package br.com.plataformaeducacional.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "escolas")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Evitar recursão em relacionamentos
public class Escola {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String nome;

    private String emailContato;

    private String telefone;

    // Relacionamentos
    @OneToMany(mappedBy = "escola")
    private List<Turma> turmas;
}

