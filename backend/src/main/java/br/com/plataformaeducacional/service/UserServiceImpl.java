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
import br.com.plataformaeducacional.entity.MatriculaAluno;
import br.com.plataformaeducacional.repository.MatriculaAlunoRepository;
import br.com.plataformaeducacional.repository.TurmaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.plataformaeducacional.entity.Turma;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import br.com.plataformaeducacional.dto.EscolaDTO;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;
    private final EscolaRepository escolaRepository;
    private final LotacaoProfessorRepository lotacaoProfessorRepository;
    private final MatriculaAlunoRepository matriculaAlunoRepository;
    private final TurmaRepository turmaRepository;
    private final PasswordEncoder passwordEncoder;

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
        return toUserResponseDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAll() {
        return userRepository.findAll().stream()
                .map(this::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        return toUserResponseDTO(user);
    }

    @Override
    @Transactional
    public UserResponseDTO update(Long id, UserCreateRequestDTO dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

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
        // Atualizar matrícula do aluno se for ALUNO
        if (newRole == Role.ALUNO && dto.getTurmaId() != null) {
            Aluno aluno = alunoRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));
            Turma turma = turmaRepository.findById(dto.getTurmaId())
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada"));
            Escola escola = turma.getEscola();
            // Procura matrícula existente para o ano letivo e escola e turno
            MatriculaAluno matricula = matriculaAlunoRepository.findByAlunoIdAndEscolaIdAndTurnoAndAnoLetivo(
                aluno.getId(), escola.getId(), turma.getTurno(), turma.getAnoLetivo()
            ).orElse(null);
            if (matricula == null) {
                matricula = new MatriculaAluno(aluno, escola, turma.getTurno(), turma.getAnoLetivo());
            }
            matricula.setTurma(turma);
            matriculaAlunoRepository.save(matricula);
        }
        return toUserResponseDTO(savedUser);
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

    @Override
    public List<UserResponseDTO> getProfessoresByEscola(Long escolaId) {
        if (escolaId == null) {
            return userRepository.findAllByRole(Role.PROFESSOR).stream()
                    .map(this::toUserResponseDTO)
                    .collect(Collectors.toList());
        }
        List<User> professores = userRepository.findProfessoresByEscolaId(escolaId);
        if (professores == null) {
            return new ArrayList<>();
        }
        return professores.stream()
                .map(this::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EscolaDTO> getEscolasDoProfessor(Long professorId) {
        Professor professor = professorRepository.findById(professorId)
            .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado"));
        return professor.getLotacoes().stream()
            .map(lotacao -> {
                Escola escola = lotacao.getEscola();
                EscolaDTO dto = new EscolaDTO();
                dto.setId(escola.getId());
                dto.setNome(escola.getNome());
                dto.setEmailContato(escola.getEmailContato());
                dto.setTelefone(escola.getTelefone());
                return dto;
            })
            .collect(Collectors.toList());
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

    private UserResponseDTO toUserResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setNomeCompleto(user.getNomeCompleto());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setAtivo(user.isAtivo());

        if (user instanceof Professor professor) {
            professor.getLotacoes().stream().findFirst().ifPresent(lotacao -> {
                dto.setEscolaId(lotacao.getEscola().getId());
                dto.setEscolaNome(lotacao.getEscola().getNome());
            });
        } else if (user instanceof Aluno aluno) {
            aluno.getMatriculas().stream().findFirst().ifPresent(matricula -> {
                dto.setEscolaId(matricula.getEscola().getId());
                dto.setEscolaNome(matricula.getEscola().getNome());
                if (matricula.getTurma() != null) {
                    dto.setTurmaId(matricula.getTurma().getId());
                    dto.setTurmaNome(matricula.getTurma().getNome());
                }
            });
        }
        return dto;
    }
}
