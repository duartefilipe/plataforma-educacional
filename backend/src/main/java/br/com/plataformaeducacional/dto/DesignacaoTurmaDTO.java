package br.com.plataformaeducacional.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DesignacaoTurmaDTO {

    @NotNull(message = "O ID da turma é obrigatório")
    private Long turmaId;
} 