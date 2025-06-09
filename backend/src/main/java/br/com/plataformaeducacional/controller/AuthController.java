package br.com.plataformaeducacional.controller;

import br.com.plataformaeducacional.dto.request.AuthRequestDTO;
import br.com.plataformaeducacional.dto.response.AuthResponseDTO;
import br.com.plataformaeducacional.entity.User;
import br.com.plataformaeducacional.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha()));

            User user = (User) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(user);
            return ResponseEntity.ok(new AuthResponseDTO(token, user.getNomeCompleto(), user.getRole().name()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }
    }
}