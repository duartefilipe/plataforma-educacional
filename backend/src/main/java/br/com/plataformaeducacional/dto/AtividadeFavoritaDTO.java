package br.com.plataformaeducacional.dto;

import lombok.Data;

@Data
public class AtividadeFavoritaDTO {
    private Long id;
    private Long professorId;
    private Long atividadeCompartilhadaId;
} 