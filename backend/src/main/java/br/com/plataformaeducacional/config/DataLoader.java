package br.com.plataformaeducacional.config;

import br.com.plataformaeducacional.entity.User;
import br.com.plataformaeducacional.enums.Role;
import br.com.plataformaeducacional.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Cria um usuário ADMIN inicial se não existir nenhum usuário
        if (userRepository.count() == 0) {
            User adminUser = new User(
                "Administrador Principal",
                "admin@plataforma.com",
                passwordEncoder.encode("admin123"), // Senha padrão inicial
                Role.ADMIN
            );
            userRepository.save(adminUser);
            System.out.println("Usuário ADMIN inicial criado com email: admin@plataforma.com e senha: admin123");
        }
    }
}

