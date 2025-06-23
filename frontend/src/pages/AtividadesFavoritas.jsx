import React, { useEffect, useState } from 'react';
import { listarFavoritas, desfavoritarAtividade } from '../api/axiosConfig';
import api from '../api/axiosConfig';
import { Container, Typography, List, ListItem, ListItemText, IconButton, CircularProgress, Alert } from '@mui/material';
import StarIcon from '@mui/icons-material/Star';
import { useAuth } from '../context/AuthContext';

const AtividadesFavoritas = () => {
  const { user } = useAuth();
  const professorId = user?.id;
  const [favoritas, setFavoritas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!professorId) return;
    const fetchFavoritas = async () => {
      try {
        setLoading(true);
        const res = await listarFavoritas(professorId);
        const atividades = await Promise.all(res.data.map(async (fav) => {
          const resp = await api.get(`/atividades/compartilhadas/${fav.atividadeCompartilhadaId}`);
          return resp.data;
        }));
        setFavoritas(atividades);
      } catch (err) {
        setError('Erro ao carregar favoritas.');
      } finally {
        setLoading(false);
      }
    };
    fetchFavoritas();
  }, [professorId]);

  const handleDesfavoritar = async (atividadeCompartilhadaId) => {
    try {
      await desfavoritarAtividade(professorId, atividadeCompartilhadaId);
      setFavoritas(favoritas.filter(a => a.id !== atividadeCompartilhadaId));
    } catch (err) {
      alert('Erro ao desfavoritar atividade.');
    }
  };

  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>Atividades Favoritas</Typography>
      {loading ? <CircularProgress /> : error ? <Alert severity="error">{error}</Alert> : (
        <List>
          {favoritas.map((atividade) => (
            <ListItem key={atividade.id} secondaryAction={
              <IconButton edge="end" color="warning" onClick={() => handleDesfavoritar(atividade.id)}>
                <StarIcon />
              </IconButton>
            }>
              <ListItemText primary={atividade.titulo || atividade.atividadeTitulo} secondary={atividade.descricao || atividade.atividadeDescricao} />
            </ListItem>
          ))}
        </List>
      )}
    </Container>
  );
};

export default AtividadesFavoritas; 