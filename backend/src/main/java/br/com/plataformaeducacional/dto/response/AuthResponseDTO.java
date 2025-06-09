package br.com.plataformaeducacional.dto;

public class AuthResponseDTO {
    private String token;
    private String nomeCompleto;
    private String role;

    public AuthResponseDTO(String token, String nomeCompleto, String role) {
        this.token = token;
        this.nomeCompleto = nomeCompleto;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public String getRole() {
        return role;
    }
}