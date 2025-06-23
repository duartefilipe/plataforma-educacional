package br.com.plataformaeducacional.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurmaDTO {

    private Long id;

    private String nome;

    private Integer anoLetivo;

    private Long escolaId;

    private String nomeEscola;

    private Long professorId;

    private String professorNome;

    private String turno;
}
