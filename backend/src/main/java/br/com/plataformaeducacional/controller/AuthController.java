package br.com.plataformaeducacional.controller;

import br.com.plataformaeducacional.dto.request.AuthRequestDTO;
import br.com.plataformaeducacional.entity.User;
import br.com.plataformaeducacional.repository.UserRepository;
import br.com.plataformaeducacional.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElse(null);

        if (user == null || !passwordEncoder.matches(dto.getSenha(), user.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getSenha())
                .roles(user.getRole().name())
                .build();

        String token = jwtTokenUtil.generateToken(userDetails);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole().name());

        return ResponseEntity.ok(response);
    }
}
