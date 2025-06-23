import React, { useEffect, useState } from 'react';
import { Container, Typography, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, CircularProgress, Alert } from '@mui/material';
import api from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';

const ProfessorAtividades = () => {
  const { user } = useAuth();
  const [minhasAtividades, setMinhasAtividades] = useState([]);
  const [compartilhadas, setCompartilhadas] = useState([]);
  const [favoritas, setFavoritas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!user?.id) return;
    const fetchAll = async () => {
      try {
        setLoading(true);
        const [minhasRes, compartilhadasRes, favoritasRes] = await Promise.all([
          api.get('/atividades/professor/me'),
          api.get('/atividades/compartilhadas'),
          api.get(`/atividades/favoritas/professor/${user.id}`),
        ]);
        setMinhasAtividades(minhasRes.data);
        setCompartilhadas(compartilhadasRes.data.content || []);
        const favoritasArray = await Promise.all(
          favoritasRes.data.map(async (fav) => {
            const resp = await api.get(`/atividades/compartilhadas/${fav.atividadeCompartilhadaId}`);
            return resp.data;
          })
        );
        setFavoritas(favoritasArray);
      } catch (err) {
        setError('Erro ao carregar atividades.');
      } finally {
        setLoading(false);
      }
    };
    fetchAll();
  }, [user]);

  const renderTable = (title, data, columns) => (
    <Paper sx={{ mb: 4, p: 2 }}>
      <Typography variant="h6" gutterBottom>{title}</Typography>
      <TableContainer>
        <Table>
          <TableHead>
            <TableRow>
              {columns.map((col) => <TableCell key={col}>{col}</TableCell>)}
            </TableRow>
          </TableHead>
          <TableBody>
            {data.length === 0 ? (
              <TableRow><TableCell colSpan={columns.length}>Nenhuma atividade encontrada.</TableCell></TableRow>
            ) : (
              data.map((atividade) => (
                <TableRow key={atividade.id}>
                  <TableCell>{atividade.titulo || atividade.atividadeTitulo || (atividade.atividadeCompartilhada && atividade.atividadeCompartilhada.titulo)}</TableCell>
                  <TableCell>{atividade.tipoConteudo || atividade.atividadeTipoConteudo || (atividade.atividadeCompartilhada && atividade.atividadeCompartilhada.tipoConteudo)}</TableCell>
                  <TableCell>{atividade.descricao || atividade.atividadeDescricao || (atividade.atividadeCompartilhada && atividade.atividadeCompartilhada.descricao)}</TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Paper>
  );

  if (loading) return <CircularProgress />;
  if (error) return <Alert severity="error">{error}</Alert>;

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>Minhas Atividades</Typography>
      {renderTable('Minhas Atividades', minhasAtividades, ['Título', 'Tipo', 'Descrição'])}
      {renderTable('Atividades Compartilhadas', compartilhadas, ['Título', 'Tipo', 'Descrição'])}
      {renderTable('Favoritas', favoritas, ['Título', 'Tipo', 'Descrição'])}
    </Container>
  );
};

export default ProfessorAtividades; 