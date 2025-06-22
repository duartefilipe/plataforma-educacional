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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EscolaServiceImpl implements EscolaService {

    private final EscolaRepository escolaRepository;

    @Override
    @Transactional
    public EscolaDTO criarEscola(EscolaDTO escolaDTO) {
        Escola escola = new Escola();
        BeanUtils.copyProperties(escolaDTO, escola, "id");
        Escola savedEscola = escolaRepository.save(escola);
        return convertToDTO(savedEscola);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EscolaDTO> listarEscolas() {
        return escolaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EscolaDTO buscarEscolaPorId(Long id) {
        Escola escola = escolaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Escola não encontrada com ID: " + id));
        return convertToDTO(escola);
    }

    @Override
    @Transactional
    public EscolaDTO atualizarEscola(Long id, EscolaDTO escolaDTO) {
        Escola escolaExistente = escolaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Escola não encontrada para atualização com ID: " + id));
        BeanUtils.copyProperties(escolaDTO, escolaExistente, "id", "createdAt", "updatedAt");
        Escola updatedEscola = escolaRepository.save(escolaExistente);
        return convertToDTO(updatedEscola);
    }

    @Override
    @Transactional
    public void deletarEscola(Long id) {
        if (!escolaRepository.existsById(id)) {
            throw new EntityNotFoundException("Escola não encontrada para exclusão com ID: " + id);
        }
        escolaRepository.deleteById(id);
    }

    private EscolaDTO convertToDTO(Escola escola) {
        EscolaDTO dto = new EscolaDTO();
        BeanUtils.copyProperties(escola, dto);
        return dto;
    }
} 