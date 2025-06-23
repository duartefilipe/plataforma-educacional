package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.dto.request.UserCreateRequestDTO;
import br.com.plataformaeducacional.dto.response.UserResponseDTO;
import br.com.plataformaeducacional.dto.EscolaDTO;

import java.util.List;

public interface UserService {
    UserResponseDTO create(UserCreateRequestDTO request);
    List<UserResponseDTO> getAll();
    UserResponseDTO getById(Long id);
    UserResponseDTO update(Long id, UserCreateRequestDTO dto);
    void delete(Long id);
    void createUser(UserCreateRequestDTO userDTO);
    List<UserResponseDTO> getProfessoresByEscola(Long escolaId);
    List<EscolaDTO> getEscolasDoProfessor(Long professorId);
}
