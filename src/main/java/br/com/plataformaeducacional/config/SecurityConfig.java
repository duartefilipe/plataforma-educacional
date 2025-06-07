package br.com.plataformaeducacional.config;

import br.com.plataformaeducacional.enums.Role;
import br.com.plataformaeducacional.service.UserDetailsServiceImpl;
import br.com.plataformaeducacional.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                // Restrições de Admin (exemplo)
                .requestMatchers("/api/admin/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/api/escolas/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/api/escolas/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/api/escolas/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/api/professores/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/api/professores/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/api/professores/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/api/alunos/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/api/alunos/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/api/alunos/**").hasRole(Role.ADMIN.name())

                // Restrições de Professor (exemplo)
                .requestMatchers("/api/professor/**").hasAnyRole(Role.PROFESSOR.name(), Role.ADMIN.name())
                .requestMatchers("/api/atividades/**").hasAnyRole(Role.PROFESSOR.name(), Role.ADMIN.name())
                .requestMatchers("/api/atividades/compartilhar/**").hasAnyRole(Role.PROFESSOR.name(), Role.ADMIN.name())

                // Restrições de Aluno (exemplo)
                .requestMatchers("/api/aluno/**").hasAnyRole(Role.ALUNO.name(), Role.ADMIN.name())

                // Qualquer outra requisição precisa estar autenticada
                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

