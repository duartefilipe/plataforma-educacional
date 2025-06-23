import React, { useEffect, useState } from 'react';
import api from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';
import { Container, Typography, List, ListItem, ListItemText, Button, CircularProgress, Alert } from '@mui/material';
import { useNavigate } from 'react-router-dom';

const ProfessorEscolas = () => {
  const { user } = useAuth();
  const [escolas, setEscolas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    if (!user?.id) return;
    const fetchEscolas = async () => {
      try {
        setLoading(true);
        const res = await api.get('/turmas/me/escolas');
        setEscolas(res.data);
      } catch (err) {
        setError('Erro ao carregar escolas.');
      } finally {
        setLoading(false);
      }
    };
    fetchEscolas();
  }, [user]);

  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>Minhas Escolas</Typography>
      {loading ? <CircularProgress /> : error ? <Alert severity="error">{error}</Alert> : (
        <List>
          {escolas.map((escola) => (
            <ListItem key={escola.id} secondaryAction={
              <Button variant="contained" onClick={() => navigate(`/professor/escola/${escola.id}/turmas`)}>
                Ver Turmas
              </Button>
            }>
              <ListItemText primary={escola.nome} />
            </ListItem>
          ))}
        </List>
      )}
    </Container>
  );
};

export default ProfessorEscolas; 