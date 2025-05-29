package br.com.plataformaeducacional.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AtividadeCompartilhadaDTO {

    private Long id; // ID da AtividadeCompartilhada

    @NotNull(message = "ID da Atividade original é obrigatório")
    private Long atividadeId;

    // Campos da Atividade original para exibição/filtro
    private String atividadeTitulo;
    private String atividadeDescricao;
    private String atividadeTipoConteudo;
    private Long atividadeProfessorCriadorId;
    private String atividadeProfessorCriadorNome;
    private LocalDateTime atividadeCreatedAt;

    // Campos do compartilhamento
    private LocalDateTime dataCompartilhamento;
    private Integer idadeAlvoMin;
    private Integer idadeAlvoMax;
    @Size(max = 50)
    private String anoEscolar;
    @Size(max = 100)
    private String tipoAtividade;
    @Size(max = 100)
    private String disciplina;
    private String tags;
}

