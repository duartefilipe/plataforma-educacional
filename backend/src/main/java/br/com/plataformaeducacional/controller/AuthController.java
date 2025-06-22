package br.com.plataformaeducacional.controller;

import br.com.plataformaeducacional.dto.request.AuthRequestDTO;
import br.com.plataformaeducacional.dto.response.AuthResponseDTO;
import br.com.plataformaeducacional.entity.User;
import br.com.plataformaeducacional.repository.UserRepository;
import br.com.plataformaeducacional.security.JwtTokenUtil;
import br.com.plataformaeducacional.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO dto) {
        try {
            logger.info("Tentativa de login para o email: {}", dto.getEmail());
            
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
            );

            User user = (User) authentication.getPrincipal();

            final String token = jwtTokenUtil.generateToken(user);
            
            AuthResponseDTO response = new AuthResponseDTO(
                token, 
                user.getId(), 
                user.getNomeCompleto(), 
                user.getEmail(),
                user.getRole().name()
            );

            logger.info("Login realizado com sucesso para o email: {}", dto.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erro na autenticação para o email: {}", dto.getEmail(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }
    }
}
