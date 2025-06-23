package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.dto.EscolaDTO;
import br.com.plataformaeducacional.entity.Escola;
import br.com.plataformaeducacional.repository.EscolaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EscolaServiceImpl implements EscolaService {

    private final EscolaRepository escolaRepository;

    @Override
    public EscolaDTO createEscola(EscolaDTO escolaDTO) {
        Escola escola = new Escola();
        BeanUtils.copyProperties(escolaDTO, escola);
        escola = escolaRepository.save(escola);
        escolaDTO.setId(escola.getId());
        return escolaDTO;
    }

    @Override
    public List<EscolaDTO> getAllEscolas() {
        return escolaRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<EscolaDTO> getEscolaById(Long id) {
        return escolaRepository.findById(id).map(this::toDTO);
    }

    @Override
    public EscolaDTO atualizarEscola(Long id, EscolaDTO escolaDTO) {
        Escola escola = escolaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Escola não encontrada com o id: " + id));

        escola.setNome(escolaDTO.getNome());
        escola.setEmailContato(escolaDTO.getEmailContato());
        escola.setTelefone(escolaDTO.getTelefone());

        Escola escolaAtualizada = escolaRepository.save(escola);
        return toDTO(escolaAtualizada);
    }

    @Override
    public void deleteEscola(Long id) {
        escolaRepository.deleteById(id);
    }

    private EscolaDTO toDTO(Escola escola) {
        EscolaDTO dto = new EscolaDTO();
        BeanUtils.copyProperties(escola, dto);
        return dto;
    }
} 