import React, { useEffect, useState } from 'react';
import {
  Container,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  CircularProgress,
  Alert,
  Button,
  Box
} from '@mui/material';
import api from '../api/axiosConfig';
import { useNavigate } from 'react-router-dom';

const ListarAtividades = () => {
  const [atividades, setAtividades] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetchAtividades();
  }, []);

  const fetchAtividades = async () => {
    try {
      setLoading(true);
      const response = await api.get('/atividades/professor/me');
      setAtividades(response.data);
    } catch (err) {
      setError('Falha ao carregar atividades.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir esta atividade?')) {
      try {
        await api.delete(`/atividades/${id}`);
        setAtividades(atividades.filter(atividade => atividade.id !== id));
      } catch (err) {
        setError('Falha ao excluir a atividade.');
        console.error(err);
      }
    }
  };

  return (
    <Container maxWidth="lg" style={{ marginTop: '2rem' }}>
      <Typography variant="h4" gutterBottom>
        Minhas Atividades
      </Typography>
      {loading ? (
        <CircularProgress />
      ) : error ? (
        <Alert severity="error">{error}</Alert>
      ) : (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Título</TableCell>
                <TableCell>Tipo</TableCell>
                <TableCell align="right">Ações</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {atividades.map((atividade) => (
                <TableRow key={atividade.id}>
                  <TableCell>{atividade.titulo}</TableCell>
                  <TableCell>{atividade.tipoConteudo}</TableCell>
                  <TableCell align="right">
                    <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
                      <Button 
                        variant="contained" 
                        color="primary" 
                        size="small" 
                        onClick={() => navigate(`/professor/editar-atividade/${atividade.id}`)}
                      >
                        Editar
                      </Button>
                      <Button
                        variant="contained"
                        color="success"
                        size="small"
                        onClick={() => navigate(`/professor/designar-atividade/${atividade.id}`)}
                      >
                        Designar
                      </Button>
                      <Button
                        variant="contained"
                        color="secondary"
                        size="small"
                        onClick={() => handleDelete(atividade.id)}
                      >
                        Excluir
                      </Button>
                    </Box>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </Container>
  );
};

export default ListarAtividades; 