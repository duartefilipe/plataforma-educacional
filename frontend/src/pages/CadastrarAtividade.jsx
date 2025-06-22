import React, { useState } from 'react';
import { Button, Container, TextField, Typography, Select, MenuItem, FormControl, InputLabel } from '@mui/material';
import api from '../api/axiosConfig';
import { useNavigate } from 'react-router-dom';

const CadastrarAtividade = () => {
  const [titulo, setTitulo] = useState('');
  const [descricao, setDescricao] = useState('');
  const [arquivo, setArquivo] = useState(null);
  const [tipoConteudo, setTipoConteudo] = useState('TEXTO');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    const formData = new FormData();
    const atividadeDTO = { titulo, descricao, tipoConteudo };
    formData.append('atividade', new Blob([JSON.stringify(atividadeDTO)], { type: 'application/json' }));
    
    if (arquivo && tipoConteudo === 'ARQUIVO_UPLOAD') {
      formData.append('arquivo', arquivo);
    }

    try {
      await api.post('/atividades', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      alert('Atividade criada com sucesso!');
      navigate('/professor/atividades');
    } catch (err) {
      console.error("Falha ao criar atividade:", err);
      alert(`Erro ao criar atividade: ${err.response?.data?.message || 'Tente novamente.'}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="sm" style={{ marginTop: '2rem' }}>
      <Typography variant="h4" gutterBottom>Cadastrar Nova Atividade</Typography>
      <form onSubmit={handleSubmit}>
        <TextField
          label="Título"
          fullWidth
          margin="normal"
          value={titulo}
          onChange={(e) => setTitulo(e.target.value)}
          disabled={loading}
          required
        />
        <TextField
          label="Descrição"
          fullWidth
          margin="normal"
          multiline
          rows={4}
          value={descricao}
          onChange={(e) => setDescricao(e.target.value)}
          disabled={loading}
          required
        />
        <FormControl fullWidth margin="normal">
          <InputLabel>Tipo de Conteúdo</InputLabel>
          <Select
            value={tipoConteudo}
            label="Tipo de Conteúdo"
            onChange={(e) => setTipoConteudo(e.target.value)}
          >
            <MenuItem value="TEXTO">Texto</MenuItem>
            <MenuItem value="ARQUIVO_UPLOAD">Arquivo</MenuItem>
          </Select>
        </FormControl>

        {tipoConteudo === 'ARQUIVO_UPLOAD' && (
          <>
            <Button
              variant="contained"
              component="label"
              fullWidth
              style={{ marginTop: '1rem', marginBottom: '1rem' }}
            >
              Anexar Arquivo
              <input
                type="file"
                hidden
                onChange={(e) => setArquivo(e.target.files[0])}
              />
            </Button>
            {arquivo && <Typography variant="body2">{arquivo.name}</Typography>}
          </>
        )}
       
        <Button
          variant="contained"
          color="primary"
          fullWidth
          type="submit"
          disabled={loading}
          style={{ marginTop: '1rem' }}
        >
          {loading ? 'Criando...' : 'Criar Atividade'}
        </Button>
      </form>
    </Container>
  );
};

export default CadastrarAtividade; 