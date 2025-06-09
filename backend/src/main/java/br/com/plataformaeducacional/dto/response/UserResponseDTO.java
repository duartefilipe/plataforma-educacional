package br.com.plataformaeducacional.dto.response;

public class UserResponseDTO {
    private Long id;
    private String nomeCompleto;
    private String email;
    private String role;

    public UserResponseDTO(Long id, String nomeCompleto, String email, String role) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
