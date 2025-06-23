package br.com.plataformaeducacional.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EscolaDTO {
    private Long id;

    @NotBlank(message = "O nome da escola é obrigatório")
    private String nome;
    
    private String endereco;

    private String emailContato;

    private String telefone;
}
