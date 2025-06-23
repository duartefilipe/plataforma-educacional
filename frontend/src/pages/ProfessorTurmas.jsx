import React, { useEffect, useState } from 'react';
import api from '../api/axiosConfig';
import { useParams, useNavigate } from 'react-router-dom';
import { Container, Typography, List, ListItem, ListItemText, Button, CircularProgress, Alert } from '@mui/material';

const ProfessorTurmas = () => {
  const { escolaId } = useParams();
  const [turmas, setTurmas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchTurmas = async () => {
      try {
        setLoading(true);
        const res = await api.get(`/turmas?escolaId=${escolaId}`);
        setTurmas(res.data);
      } catch (err) {
        setError('Erro ao carregar turmas.');
      } finally {
        setLoading(false);
      }
    };
    fetchTurmas();
  }, [escolaId]);

  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>Turmas da Escola</Typography>
      {loading ? <CircularProgress /> : error ? <Alert severity="error">{error}</Alert> : (
        <List>
          {turmas.map((turma) => (
            <ListItem key={turma.id} secondaryAction={
              <Button variant="contained" onClick={() => navigate(`/professor/turma/${turma.id}/atividades`)}>
                Ver Atividades
              </Button>
            }>
              <ListItemText primary={turma.nome} secondary={`Ano Letivo: ${turma.anoLetivo}`} />
            </ListItem>
          ))}
        </List>
      )}
    </Container>
  );
};

export default ProfessorTurmas; 