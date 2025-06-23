package br.com.plataformaeducacional.controller;

import br.com.plataformaeducacional.dto.request.UserCreateRequestDTO;
import br.com.plataformaeducacional.dto.response.UserResponseDTO;
import br.com.plataformaeducacional.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import br.com.plataformaeducacional.dto.AtividadeDTO;
import br.com.plataformaeducacional.dto.EscolaDTO;

import br.com.plataformaeducacional.service.AtividadeService;
import br.com.plataformaeducacional.service.LotacaoProfessorService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {
    private final UserService userService;
    private final AtividadeService atividadeService;
    private final LotacaoProfessorService lotacaoProfessorService;

    @PostMapping("/criar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserCreateRequestDTO userDTO) {
        UserResponseDTO createdUser = userService.create(userDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody @Valid UserCreateRequestDTO userDTO) {
        return ResponseEntity.ok(userService.update(id, userDTO));
    }

    @GetMapping("/professores")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<List<UserResponseDTO>> getProfessoresByEscola(@RequestParam(required = false) Long escolaId) {
        return ResponseEntity.ok(userService.getProfessoresByEscola(escolaId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/escolas")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<List<EscolaDTO>> getEscolasDoProfessor(@AuthenticationPrincipal br.com.plataformaeducacional.entity.User user) {
        return ResponseEntity.ok(userService.getEscolasDoProfessor(user.getId()));
    }

    @GetMapping("/turmas/{turmaId}/atividades")
    public ResponseEntity<List<AtividadeDTO>> listarAtividadesPorTurma(@PathVariable Long turmaId) {
        return ResponseEntity.ok(atividadeService.listarAtividadesPorTurma(turmaId));
    }

    @PostMapping("/professores/{professorId}/escolas/{escolaId}/vincular")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> vincularProfessorEscola(@PathVariable Long professorId, @PathVariable Long escolaId) {
        lotacaoProfessorService.vincularProfessorEscola(professorId, escolaId);
        return ResponseEntity.ok().build();
    }
}
