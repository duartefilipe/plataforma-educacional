package br.com.plataformaeducacional.dto.response;

import br.com.plataformaeducacional.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDTO {

    private Long id;
    private String nomeCompleto;
    private String email;
    private Role role;
    private boolean ativo;
    private Long escolaId;
    private String escolaNome;
    private Long turmaId;
    private String turmaNome;
}
