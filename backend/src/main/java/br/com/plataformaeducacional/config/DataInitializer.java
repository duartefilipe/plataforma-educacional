package br.com.plataformaeducacional.config;

import br.com.plataformaeducacional.entity.User;
import br.com.plataformaeducacional.enums.Role;
import br.com.plataformaeducacional.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User(
                    "Admin",
                    "admin@escola.com",
                    new BCryptPasswordEncoder().encode("admin123"),
                    Role.ADMIN
                );
                userRepository.save(admin);
                System.out.println("✅ Usuário admin criado com sucesso!");
            }
        };
    }
}
