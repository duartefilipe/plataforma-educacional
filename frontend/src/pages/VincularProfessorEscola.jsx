import React, { useEffect, useState } from 'react';
import { Box, Button, FormControl, InputLabel, MenuItem, Select, Typography, Alert } from '@mui/material';
import api from '../api/axiosConfig';

const VincularProfessorEscola = () => {
  const [professores, setProfessores] = useState([]);
  const [escolas, setEscolas] = useState([]);
  const [professorId, setProfessorId] = useState('');
  const [escolaId, setEscolaId] = useState('');
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        const profRes = await api.get('/users/professores');
        setProfessores(profRes.data);
        const escRes = await api.get('/escolas');
        setEscolas(escRes.data);
      } catch (err) {
        setError('Erro ao carregar professores ou escolas.');
      }
    };
    fetchData();
  }, []);

  const handleVincular = async () => {
    setSuccess('');
    setError('');
    if (!professorId || !escolaId) {
      setError('Selecione um professor e uma escola.');
      return;
    }
    try {
      await api.post(`/users/professores/${professorId}/escolas/${escolaId}/vincular`);
      setSuccess('Professor vinculado à escola com sucesso!');
    } catch (err) {
      setError('Erro ao vincular professor à escola.');
    }
  };

  return (
    <Box sx={{ maxWidth: 400, mx: 'auto', mt: 4 }}>
      <Typography variant="h5" gutterBottom>Vincular Professor a Escola</Typography>
      {success && <Alert severity="success">{success}</Alert>}
      {error && <Alert severity="error">{error}</Alert>}
      <FormControl fullWidth sx={{ mt: 2 }}>
        <InputLabel>Professor</InputLabel>
        <Select value={professorId} label="Professor" onChange={e => setProfessorId(e.target.value)}>
          {professores.map(prof => (
            <MenuItem key={prof.id} value={prof.id}>{prof.nomeCompleto || prof.nome}</MenuItem>
          ))}
        </Select>
      </FormControl>
      <FormControl fullWidth sx={{ mt: 2 }}>
        <InputLabel>Escola</InputLabel>
        <Select value={escolaId} label="Escola" onChange={e => setEscolaId(e.target.value)}>
          {escolas.map(esc => (
            <MenuItem key={esc.id} value={esc.id}>{esc.nome}</MenuItem>
          ))}
        </Select>
      </FormControl>
      <Button variant="contained" color="primary" sx={{ mt: 3 }} onClick={handleVincular}>
        Vincular
      </Button>
    </Box>
  );
};

export default VincularProfessorEscola; 