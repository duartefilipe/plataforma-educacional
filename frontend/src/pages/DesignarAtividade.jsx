import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Container,
  Typography,
  Paper,
  Box,
  CircularProgress,
  Alert,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  List,
  ListItem,
  ListItemText
} from '@mui/material';
import api from '../api/axiosConfig';

const DesignarAtividade = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [atividade, setAtividade] = useState(null);
  const [turmas, setTurmas] = useState([]);
  const [selectedTurma, setSelectedTurma] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        // Busca a atividade e as turmas em paralelo
        const [atividadeRes, turmasRes] = await Promise.all([
          api.get(`/atividades/${id}`),
          api.get('/turmas') // Idealmente, deveria buscar apenas as turmas do professor/escola
        ]);
        setAtividade(atividadeRes.data);
        setTurmas(turmasRes.data);
      } catch (err) {
        setError('Erro ao carregar dados. ' + (err.response?.data?.message || err.message));
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [id]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedTurma) {
      setError('Por favor, selecione uma turma.');
      return;
    }
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      await api.post(`/atividades/${id}/designar-turma`, { turmaId: selectedTurma });
      setSuccess(`Atividade designada com sucesso para a turma selecionada!`);
      setTimeout(() => navigate('/professor/atividades'), 2000);
    } catch (err) {
      setError('Erro ao designar atividade. ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  if (loading && !atividade) {
    return <CircularProgress />;
  }

  return (
    <Container component={Paper} maxWidth="md" sx={{ mt: 4, p: 4 }}>
      <Typography variant="h4" gutterBottom>Designar Atividade</Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}
      
      {atividade && (
        <Box sx={{ mb: 4 }}>
          <Typography variant="h6">Detalhes da Atividade</Typography>
          <List>
            <ListItem><ListItemText primary="Título" secondary={atividade.titulo} /></ListItem>
            <ListItem><ListItemText primary="Descrição" secondary={atividade.descricao} /></ListItem>
            <ListItem><ListItemText primary="Tipo" secondary={atividade.tipoConteudo} /></ListItem>
          </List>
        </Box>
      )}

      <form onSubmit={handleSubmit}>
        <FormControl fullWidth required sx={{ mb: 2 }}>
          <InputLabel>Selecione a Turma</InputLabel>
          <Select
            value={selectedTurma}
            label="Selecione a Turma"
            onChange={(e) => setSelectedTurma(e.target.value)}
          >
            {turmas.map((turma) => (
              <MenuItem key={turma.id} value={turma.id}>
                {turma.nome} - {turma.anoLetivo}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
        <Button type="submit" variant="contained" color="primary" disabled={loading}>
          {loading ? <CircularProgress size={24} /> : 'Designar Atividade'}
        </Button>
      </form>
    </Container>
  );
};

export default DesignarAtividade; 