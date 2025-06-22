package br.com.plataformaeducacional.dto.response;

public class AuthResponseDTO {
    private String token;
    private Long id;
    private String nomeCompleto;
    private String email;
    private String role;

    public AuthResponseDTO(String token, Long id, String nomeCompleto, String email, String role) {
        this.token = token;
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
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