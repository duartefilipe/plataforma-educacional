import React, { useEffect, useState } from 'react';
import api from '../api/axiosConfig';
import { Container, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, CircularProgress, Alert, IconButton } from '@mui/material';
import { Edit, Delete } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';

const AdminAtividadesCompartilhadas = () => {
  const [atividades, setAtividades] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const res = await api.get('/atividades/compartilhadas');
        const data = Array.isArray(res.data.content) ? res.data.content : [];
        setAtividades(data);
      } catch (err) {
        setError('Erro ao carregar atividades.');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const handleEdit = (id) => {
    navigate(`/editar-atividade/${id}`);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja deletar esta atividade?')) {
      try {
        await api.delete(`/atividades/${id}`);
        setAtividades(atividades.filter(a => a.id !== id));
      } catch (err) {
        setError('Erro ao deletar atividade.');
      }
    }
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>Atividades Compartilhadas (Admin)</Typography>
      {loading ? <CircularProgress /> : error ? <Alert severity="error">{error}</Alert> : (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Título</TableCell>
                <TableCell>Descrição</TableCell>
                <TableCell>Professor</TableCell>
                <TableCell>Escola</TableCell>
                <TableCell>Data Compartilhamento</TableCell>
                <TableCell>Ações</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {Array.isArray(atividades) && atividades.map((atividade) => (
                <TableRow key={atividade.id}>
                  <TableCell>{atividade.id}</TableCell>
                  <TableCell>{atividade.atividadeTitulo}</TableCell>
                  <TableCell>{atividade.atividadeDescricao}</TableCell>
                  <TableCell>{atividade.atividadeProfessorCriadorNome}</TableCell>
                  <TableCell>{atividade.escolaNome}</TableCell>
                  <TableCell>{atividade.dataCompartilhamento}</TableCell>
                  <TableCell>
                    <IconButton onClick={() => handleEdit(atividade.id)} color="primary"><Edit /></IconButton>
                    <IconButton onClick={() => handleDelete(atividade.id)} color="error"><Delete /></IconButton>
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

export default AdminAtividadesCompartilhadas; 