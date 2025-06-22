package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.dto.request.UserCreateRequestDTO;
import br.com.plataformaeducacional.dto.response.UserResponseDTO;
import br.com.plataformaeducacional.entity.Aluno;
import br.com.plataformaeducacional.entity.Professor;
import br.com.plataformaeducacional.entity.User;
import br.com.plataformaeducacional.enums.Role;
import br.com.plataformaeducacional.repository.AlunoRepository;
import br.com.plataformaeducacional.repository.ProfessorRepository;
import br.com.plataformaeducacional.repository.UserRepository;
import br.com.plataformaeducacional.repository.EscolaRepository;
import br.com.plataformaeducacional.repository.LotacaoProfessorRepository;
import br.com.plataformaeducacional.entity.LotacaoProfessor;
import br.com.plataformaeducacional.entity.Escola;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;
    private final EscolaRepository escolaRepository;
    private final LotacaoProfessorRepository lotacaoProfessorRepository;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           ProfessorRepository professorRepository,
                           AlunoRepository alunoRepository,
                           EscolaRepository escolaRepository,
                           LotacaoProfessorRepository lotacaoProfessorRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.professorRepository = professorRepository;
        this.alunoRepository = alunoRepository;
        this.escolaRepository = escolaRepository;
        this.lotacaoProfessorRepository = lotacaoProfessorRepository;
    }

    @Override
    @Transactional
    public UserResponseDTO create(UserCreateRequestDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setNomeCompleto(dto.getNomeCompleto());
        user.setSenha(passwordEncoder.encode(dto.getSenha()));
        user.setRole(Role.valueOf(dto.getRole()));
        user.setAtivo(true);
        User savedUser = userRepository.save(user);

        handleRoleSpecificEntityCreation(savedUser, dto);
        return toDTO(savedUser);
    }

    @Override
    public List<UserResponseDTO> getAll() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return toDTO(user);
    }

    @Override
    @Transactional
    public UserResponseDTO update(Long id, UserCreateRequestDTO dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Role oldRole = user.getRole();
        Role newRole = Role.valueOf(dto.getRole());

        user.setNomeCompleto(dto.getNomeCompleto());
        user.setEmail(dto.getEmail());
        user.setRole(newRole);
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            user.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        if (oldRole != newRole) {
            // Role changed, so we need to adjust associated entities
            if (oldRole == Role.PROFESSOR) {
                professorRepository.deleteById(user.getId());
            } else if (oldRole == Role.ALUNO) {
                alunoRepository.deleteById(user.getId());
            }

            handleRoleSpecificEntityCreation(user, dto);
        }

        User savedUser = userRepository.save(user);
        return toDTO(savedUser);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void createUser(UserCreateRequestDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new RuntimeException("O e-mail informado já está em uso.");
        }
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setNomeCompleto(userDTO.getNomeCompleto());
        user.setSenha(passwordEncoder.encode(userDTO.getSenha()));
        user.setRole(Role.valueOf(userDTO.getRole()));
        user.setAtivo(true);
        User savedUser = userRepository.save(user);

        handleRoleSpecificEntityCreation(savedUser, userDTO);
    }

    private void handleRoleSpecificEntityCreation(User user, UserCreateRequestDTO dto) {
        switch (user.getRole()) {
            case PROFESSOR:
                if (!professorRepository.existsById(user.getId())) {
                    Professor professor = new Professor(user, "");
                    professorRepository.save(professor);
                    
                    if (dto.getEscolaId() != null) {
                        Escola escola = escolaRepository.findById(dto.getEscolaId())
                                .orElseThrow(() -> new EntityNotFoundException("Escola não encontrada com ID: " + dto.getEscolaId()));
                        LotacaoProfessor lotacao = new LotacaoProfessor(professor, escola);
                        lotacaoProfessorRepository.save(lotacao);
                    }
                }
                break;
            case ALUNO:
                if (!alunoRepository.existsById(user.getId())) {
                    Aluno aluno = new Aluno(user);
                    alunoRepository.save(aluno);
                }
                break;
            default:
                break;
        }
    }

    private UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getNomeCompleto(), user.getEmail(), user.getRole().name());
    }
}
