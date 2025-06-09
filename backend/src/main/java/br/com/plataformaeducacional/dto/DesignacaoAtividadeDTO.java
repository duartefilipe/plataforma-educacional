package br.com.plataformaeducacional.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DesignacaoAtividadeDTO {

    private Long id; // ID da Designação

    // Dados da Atividade Original
    private Long atividadeId;
    private String atividadeTitulo;
    private String atividadeDescricao;
    private String atividadeTipoConteudo; // TEXTO, ARQUIVO_UPLOAD
    private String atividadeNomeArquivoOriginal; // Para link de download
    private String respostaAlunoArquivo;


    // Dados do Professor que designou
    private Long professorDesignadorId;
    private String professorDesignadorNome;

    // Dados do Aluno (geralmente não precisa no DTO de retorno para o próprio aluno)
    // private Long alunoId;
    // private String alunoNome;

    // Dados da Designação
    private LocalDateTime dataDesignacao;
    private String status; // PENDENTE, VISUALIZADA, ENTREGUE, AVALIADA
    private LocalDateTime dataEntrega;
    private LocalDateTime dataAvaliacao;
    private BigDecimal nota;
    private String observacoesProfessor;
    private String respostaAlunoTexto;
    // Não incluir caminho do arquivo de resposta por segurança

    // DTO pode ter variações: uma para o professor ver, outra para o aluno
    // Esta é uma versão mais focada no que o aluno veria.
}

