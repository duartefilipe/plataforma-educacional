package br.com.plataformaeducacional.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EscolaDTO {
    private Long id;

    @NotBlank(message = "Nome da escola é obrigatório")
    private String nome;
    
    private String endereco;
}
