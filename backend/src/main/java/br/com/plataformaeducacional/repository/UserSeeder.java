package br.com.plataformaeducacional.repository;

import br.com.plataformaeducacional.entity.User;
import br.com.plataformaeducacional.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.annotation.PostConstruct;

@Configuration
public class UserSeeder {

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            User admin = new User(
                "Administrador",
                "admin@escola.com",
                new BCryptPasswordEncoder().encode("admin123"),
                Role.ADMIN
            );
            userRepository.save(admin);
            System.out.println("✅ Usuário ADMIN criado: admin@escola.com / senha: admin123");
        }
    }
}
