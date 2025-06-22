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

const ListarUsuarios = ({ role, title }) => {
  const [usuarios, setUsuarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetchUsuarios();
  }, [role]);

  const fetchUsuarios = async () => {
    try {
      setLoading(true);
      const response = await api.get('/users');
      const filteredUsers = response.data.filter(user => user.role === role);
      setUsuarios(filteredUsers);
    } catch (err) {
      setError('Falha ao carregar usuários. Tente novamente mais tarde.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este usuário?')) {
      try {
        await api.delete(`/users/${id}`);
        // Atualiza a lista removendo o usuário excluído
        setUsuarios(usuarios.filter(usuario => usuario.id !== id));
      } catch (err) {
        setError('Falha ao excluir o usuário.');
        console.error(err);
      }
    }
  };

  return (
    <Container maxWidth="lg" style={{ marginTop: '2rem' }}>
      <Typography variant="h4" gutterBottom>
        {title}
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
                <TableCell>ID</TableCell>
                <TableCell>Nome Completo</TableCell>
                <TableCell>Email</TableCell>
                <TableCell align="right">Ações</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {usuarios.map((usuario) => (
                <TableRow key={usuario.id}>
                  <TableCell>{usuario.id}</TableCell>
                  <TableCell>{usuario.nomeCompleto}</TableCell>
                  <TableCell>{usuario.email}</TableCell>
                  <TableCell align="right">
                    <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
                      <Button 
                        variant="contained" 
                        color="primary" 
                        size="small" 
                        onClick={() => navigate(`/editar-usuario/${usuario.id}`)}
                      >
                        Editar
                      </Button>
                      <Button
                        variant="contained"
                        color="secondary"
                        size="small"
                        onClick={() => handleDelete(usuario.id)}
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

export default ListarUsuarios; 