import React, { useEffect, useState } from 'react';
import api, { favoritarAtividade, desfavoritarAtividade, listarFavoritas } from '../api/axiosConfig';
import { Container, Typography, List, ListItem, ListItemText, IconButton, CircularProgress, Alert } from '@mui/material';
import StarIcon from '@mui/icons-material/Star';
import StarBorderIcon from '@mui/icons-material/StarBorder';
import { useAuth } from '../context/AuthContext';

const ListarAtividadesCompartilhadas = () => {
  const { user } = useAuth();
  const professorId = user?.id;
  const [atividades, setAtividades] = useState([]);
  const [favoritas, setFavoritas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!professorId) return;
    const fetchData = async () => {
      try {
        setLoading(true);
        const [atividadesRes, favoritasRes] = await Promise.all([
          api.get('/atividades/compartilhadas'),
          listarFavoritas(professorId)
        ]);
        const data = Array.isArray(atividadesRes.data.content) ? atividadesRes.data.content : [];
        setAtividades(data);
        setFavoritas(favoritasRes.data.map(f => f.atividadeCompartilhadaId));
      } catch (err) {
        setError('Erro ao carregar atividades.');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [professorId]);

  const handleFavoritar = async (atividadeCompartilhadaId) => {
    try {
      await favoritarAtividade(professorId, atividadeCompartilhadaId);
      setFavoritas([...favoritas, atividadeCompartilhadaId]);
    } catch (err) {
      alert('Erro ao favoritar atividade.');
    }
  };

  const handleDesfavoritar = async (atividadeCompartilhadaId) => {
    try {
      await desfavoritarAtividade(professorId, atividadeCompartilhadaId);
      setFavoritas(favoritas.filter(id => id !== atividadeCompartilhadaId));
    } catch (err) {
      alert('Erro ao desfavoritar atividade.');
    }
  };

  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>Atividades Compartilhadas</Typography>
      {loading ? <CircularProgress /> : error ? <Alert severity="error">{error}</Alert> : (
        <List>
          {Array.isArray(atividades) && atividades.map((atividade) => {
            const isFavorita = favoritas.includes(atividade.id);
            return (
              <ListItem key={atividade.id} secondaryAction={
                <IconButton edge="end" color="warning" onClick={() => isFavorita ? handleDesfavoritar(atividade.id) : handleFavoritar(atividade.id)}>
                  {isFavorita ? <StarIcon /> : <StarBorderIcon />}
                </IconButton>
              }>
                <ListItemText primary={atividade.titulo || atividade.atividadeTitulo} secondary={atividade.descricao || atividade.atividadeDescricao} />
              </ListItem>
            );
          })}
        </List>
      )}
    </Container>
  );
};

export default ListarAtividadesCompartilhadas; 