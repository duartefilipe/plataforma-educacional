package br.com.plataformaeducacional.controller;

import br.com.plataformaeducacional.dto.request.AuthRequestDTO;
import br.com.plataformaeducacional.entity.User;
import br.com.plataformaeducacional.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO dto) {
        logger.info("Tentativa de login para o email: {}", dto.getEmail());
        User user = userRepository.findByEmail(dto.getEmail()).orElse(null);

        if (user == null) {
            logger.warn("Usuário não encontrado para o email: {}", dto.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }

        boolean senhaCorreta = passwordEncoder.matches(dto.getSenha(), user.getSenha());
        logger.info("Senha informada: {} | Hash no banco: {} | Resultado: {}", dto.getSenha(), user.getSenha(), senhaCorreta);

        if (!senhaCorreta) {
            logger.warn("Senha incorreta para o email: {}", dto.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login realizado com sucesso");
        response.put("role", user.getRole().name());
        response.put("email", user.getEmail());
        response.put("id", user.getId());

        logger.info("Login realizado com sucesso para o email: {}", dto.getEmail());
        return ResponseEntity.ok(response);
    }
}
