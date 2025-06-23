import React, { useState, useEffect } from 'react';
import { Button, Container, TextField, Typography, MenuItem, FormControl, InputLabel, Select } from '@mui/material';
import api from '../api/axiosConfig';
import { useNavigate } from 'react-router-dom';

const CadastrarAtividadeCompartilhada = () => {
  const [atividades, setAtividades] = useState([]);
  const [atividadeId, setAtividadeId] = useState('');
  const [idadeAlvoMin, setIdadeAlvoMin] = useState('');
  const [idadeAlvoMax, setIdadeAlvoMax] = useState('');
  const [anoEscolar, setAnoEscolar] = useState('');
  const [tipoAtividade, setTipoAtividade] = useState('');
  const [disciplina, setDisciplina] = useState('');
  const [tags, setTags] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    // Buscar atividades do professor para compartilhar
    const fetchAtividades = async () => {
      try {
        const res = await api.get('/atividades/professor/me');
        setAtividades(res.data);
      } catch (err) {
        setAtividades([]);
      }
    };
    fetchAtividades();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await api.post(`/atividades/compartilhadas/compartilhar/${atividadeId}`, {
        atividadeId,
        idadeAlvoMin: idadeAlvoMin ? parseInt(idadeAlvoMin) : null,
        idadeAlvoMax: idadeAlvoMax ? parseInt(idadeAlvoMax) : null,
        anoEscolar,
        tipoAtividade,
        disciplina,
        tags
      });
      alert('Atividade compartilhada com sucesso!');
      navigate('/atividades-compartilhadas');
    } catch (err) {
      alert('Erro ao compartilhar atividade.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="sm" style={{ marginTop: '2rem' }}>
      <Typography variant="h4" gutterBottom>Compartilhar Nova Atividade</Typography>
      <form onSubmit={handleSubmit}>
        <FormControl fullWidth margin="normal" required>
          <InputLabel>Atividade</InputLabel>
          <Select
            value={atividadeId}
            label="Atividade"
            onChange={(e) => setAtividadeId(e.target.value)}
          >
            {atividades.map((a) => (
              <MenuItem key={a.id} value={a.id}>{a.titulo}</MenuItem>
            ))}
          </Select>
        </FormControl>
        <TextField
          label="Idade Alvo Mínima"
          fullWidth
          margin="normal"
          type="number"
          value={idadeAlvoMin}
          onChange={(e) => setIdadeAlvoMin(e.target.value)}
        />
        <TextField
          label="Idade Alvo Máxima"
          fullWidth
          margin="normal"
          type="number"
          value={idadeAlvoMax}
          onChange={(e) => setIdadeAlvoMax(e.target.value)}
        />
        <TextField
          label="Ano Escolar"
          fullWidth
          margin="normal"
          value={anoEscolar}
          onChange={(e) => setAnoEscolar(e.target.value)}
        />
        <TextField
          label="Tipo de Atividade"
          fullWidth
          margin="normal"
          value={tipoAtividade}
          onChange={(e) => setTipoAtividade(e.target.value)}
        />
        <TextField
          label="Disciplina"
          fullWidth
          margin="normal"
          value={disciplina}
          onChange={(e) => setDisciplina(e.target.value)}
        />
        <TextField
          label="Tags"
          fullWidth
          margin="normal"
          value={tags}
          onChange={(e) => setTags(e.target.value)}
        />
        <Button
          variant="contained"
          color="primary"
          fullWidth
          type="submit"
          disabled={loading}
          style={{ marginTop: '1rem' }}
        >
          {loading ? 'Compartilhando...' : 'Compartilhar Atividade'}
        </Button>
      </form>
    </Container>
  );
};

export default CadastrarAtividadeCompartilhada; 