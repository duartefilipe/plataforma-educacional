package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.dto.request.UserCreateRequestDTO;
import br.com.plataformaeducacional.dto.response.UserResponseDTO;

import java.util.List;

public interface UserService {
    UserResponseDTO create(UserCreateRequestDTO request);
    List<UserResponseDTO> getAll();
    UserResponseDTO getById(Long id);
    UserResponseDTO update(Long id, UserCreateRequestDTO dto);
    void delete(Long id);
    void createUser(UserCreateRequestDTO userDTO);
}
