package br.com.plataformaeducacional.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    @NotBlank(message = "Nome da escola é obrigatório")
    @Size(max = 255)
    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String endereco;

    @Size(max = 50)
    @Column(name = "contato_telefone")
    private String contatoTelefone;

    @Email(message = "Email de contato inválido")
    @Size(max = 255)
    @Column(name = "contato_email")
    private String contatoEmail;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relacionamentos
    @OneToMany(mappedBy = "escola", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude // Evitar recursão no toString
    private Set<LotacaoProfessor> lotacoes = new HashSet<>();

    @OneToMany(mappedBy = "escola", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude // Evitar recursão no toString
    private Set<MatriculaAluno> matriculas = new HashSet<>();

    public Escola(String nome, String endereco, String contatoTelefone, String contatoEmail) {
        this.nome = nome;
        this.endereco = endereco;
        this.contatoTelefone = contatoTelefone;
        this.contatoEmail = contatoEmail;
    }
}

