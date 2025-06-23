package br.com.plataformaeducacional.service;

import br.com.plataformaeducacional.dto.TarefaDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AlunoTarefaService {

    List<TarefaDTO> listarTarefasParaAluno(Long alunoId);
    TarefaDTO buscarDetalhesTarefa(Long tarefaId, Long alunoId);
    TarefaDTO marcarTarefaComoVisualizada(Long tarefaId, Long alunoId);
    TarefaDTO submeterRespostaTarefa(Long tarefaId, Long alunoId, String respostaTexto, MultipartFile arquivoResposta) throws IOException;

}

