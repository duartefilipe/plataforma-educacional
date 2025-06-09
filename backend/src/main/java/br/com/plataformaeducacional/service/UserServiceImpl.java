package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.dto.request.UserCreateRequestDTO;
import br.com.plataformaeducacional.dto.response.UserResponseDTO;
import br.com.plataformaeducacional.entity.User;
import br.com.plataformaeducacional.enums.Role;
import br.com.plataformaeducacional.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDTO create(UserCreateRequestDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setNomeCompleto(dto.getNomeCompleto());
        user.setSenha(passwordEncoder.encode(dto.getSenha()));
        user.setRole(Role.valueOf(dto.getRole()));
        user.setAtivo(true);
        return toDTO(userRepository.save(user));
    }

    @Override
    public List<UserResponseDTO> getAll() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getById(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        return toDTO(user);
    }

    @Override
    public UserResponseDTO update(Long id, UserCreateRequestDTO dto) {
        User user = userRepository.findById(id).orElseThrow();
        user.setNomeCompleto(dto.getNomeCompleto());
        user.setEmail(dto.getEmail());
        user.setRole(Role.valueOf(dto.getRole()));
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            user.setSenha(passwordEncoder.encode(dto.getSenha()));
        }
        return toDTO(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getNomeCompleto(), user.getEmail(), user.getRole().name());
    }
}
