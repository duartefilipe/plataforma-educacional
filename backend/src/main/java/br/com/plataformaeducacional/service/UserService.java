package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.dto.UserCreateRequestDTO;
import br.com.plataformaeducacional.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    UserResponseDTO create(UserCreateRequestDTO dto);
    List<UserResponseDTO> getAll();
    UserResponseDTO getById(Long id);
    UserResponseDTO update(Long id, UserCreateRequestDTO dto);
    void delete(Long id);
}
