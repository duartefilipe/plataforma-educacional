import React, { useEffect, useState } from 'react';
import { Container, Typography, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, CircularProgress, Alert, Grid, IconButton, Tooltip } from '@mui/material';
import api from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { Edit, Delete, Star, StarBorder } from '@mui/icons-material';

const ProfessorAtividades = () => {
  const { user } = useAuth();
  const [minhasAtividades, setMinhasAtividades] = useState([]);
  const [compartilhadas, setCompartilhadas] = useState([]);
  const [favoritas, setFavoritas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

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

  const handleDeleteAtividade = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir esta atividade?')) {
      try {
        await api.delete(`/atividades/${id}`);
        setMinhasAtividades(minhasAtividades.filter(a => a.id !== id));
      } catch (err) {
        alert('Erro ao excluir atividade.');
      }
    }
  };

  // Função para favoritar uma atividade compartilhada
  const handleFavorite = async (atividadeCompartilhadaId) => {
    try {
      await api.post(`/atividades/favoritas?atividadeCompartilhadaId=${atividadeCompartilhadaId}`);
      // Atualiza a lista de favoritas
      const resp = await api.get(`/atividades/compartilhadas/${atividadeCompartilhadaId}`);
      setFavoritas([...favoritas, resp.data]);
    } catch (err) {
      alert('Erro ao favoritar atividade.');
    }
  };

  // Função para desfavoritar uma atividade compartilhada
  const handleUnfavorite = async (atividadeCompartilhadaId) => {
    try {
      await api.delete(`/atividades/favoritas?atividadeCompartilhadaId=${atividadeCompartilhadaId}`);
      setFavoritas(favoritas.filter(fav => fav.id !== atividadeCompartilhadaId));
    } catch (err) {
      alert('Erro ao remover dos favoritos.');
    }
  };

  const renderTable = (title, data, columns, type) => (
    <Paper sx={{ p: 2, height: '100%' }}>
      <Typography variant="h6" gutterBottom sx={{ fontSize: '1.1rem', fontWeight: 'bold' }}>
        {title}
      </Typography>
      <TableContainer sx={{ maxHeight: 400 }}>
        <Table size="small">
          <TableHead>
            <TableRow>
              {columns.map((col) => (
                <TableCell key={col} sx={{ fontWeight: 'bold', fontSize: '0.85rem' }}>{col}</TableCell>
              ))}
              <TableCell sx={{ fontWeight: 'bold', fontSize: '0.85rem' }}>Ações</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {data.length === 0 ? (
              <TableRow>
                <TableCell colSpan={columns.length + 1} sx={{ textAlign: 'center', color: 'text.secondary' }}>
                  Nenhuma atividade encontrada.
                </TableCell>
              </TableRow>
            ) : (
              data.map((atividade) => {
                // Para atividades compartilhadas e favoritas, o id correto é o da atividade compartilhada
                const atividadeCompartilhadaId = atividade.id;
                const isFavorita = favoritas.some(fav => fav.id === atividadeCompartilhadaId);
                return (
                  <TableRow key={atividadeCompartilhadaId} hover>
                    <TableCell sx={{ fontSize: '0.8rem' }}>
                      {atividade.titulo || atividade.atividadeTitulo || (atividade.atividadeCompartilhada && atividade.atividadeCompartilhada.titulo)}
                    </TableCell>
                    {/* Ações para cada tipo de tabela */}
                    {type === 'minhas' && (
                      <TableCell>
                        <Tooltip title="Editar">
                          <IconButton color="primary" onClick={() => navigate(`/professor/editar-atividade/${atividade.id}`)} size="small">
                            <Edit fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Excluir">
                          <IconButton color="error" onClick={() => handleDeleteAtividade(atividade.id)} size="small">
                            <Delete fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      </TableCell>
                    )}
                    {type === 'compartilhadas' && (
                      <TableCell>
                        <Tooltip title="Editar">
                          <IconButton color="primary" onClick={() => navigate(`/professor/editar-atividade/${atividade.atividadeId || atividade.id}`)} size="small">
                            <Edit fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Excluir">
                          <IconButton color="error" onClick={() => handleDeleteAtividade(atividade.atividadeId || atividade.id)} size="small">
                            <Delete fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        {/* Botão de favoritar, só se não for favorita */}
                        {!isFavorita && (
                          <Tooltip title="Favoritar">
                            <IconButton color="warning" onClick={() => handleFavorite(atividadeCompartilhadaId)} size="small">
                              <StarBorder fontSize="small" />
                            </IconButton>
                          </Tooltip>
                        )}
                        {/* Se já for favorita, mostra estrela cheia */}
                        {isFavorita && (
                          <Tooltip title="Já está nos favoritos">
                            <span>
                              <IconButton color="warning" disabled size="small">
                                <Star fontSize="small" />
                              </IconButton>
                            </span>
                          </Tooltip>
                        )}
                      </TableCell>
                    )}
                    {type === 'favoritas' && (
                      <TableCell>
                        <Tooltip title="Editar">
                          <IconButton color="primary" onClick={() => navigate(`/professor/editar-atividade/${atividade.atividadeId || atividade.id}`)} size="small">
                            <Edit fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Excluir">
                          <IconButton color="error" onClick={() => handleDeleteAtividade(atividade.atividadeId || atividade.id)} size="small">
                            <Delete fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Remover dos favoritos">
                          <span>
                            <IconButton color="warning" onClick={() => handleUnfavorite(atividadeCompartilhadaId)} size="small">
                              <StarBorder fontSize="small" />
                            </IconButton>
                          </span>
                        </Tooltip>
                      </TableCell>
                    )}
                  </TableRow>
                );
              })
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Paper>
  );

  if (loading) return <CircularProgress />;
  if (error) return <Alert severity="error">{error}</Alert>;

  return (
    <Container maxWidth="xl" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom sx={{ mb: 3 }}>
        Minhas Atividades
      </Typography>
      
      <Grid container spacing={3}>
        <Grid item xs={12} md={4}>
          {renderTable('Minhas Atividades', minhasAtividades, ['Título'], 'minhas')}
        </Grid>
        <Grid item xs={12} md={4}>
          {renderTable('Atividades Compartilhadas', compartilhadas, ['Título'], 'compartilhadas')}
        </Grid>
        <Grid item xs={12} md={4}>
          {renderTable('Favoritas', favoritas, ['Título'], 'favoritas')}
        </Grid>
      </Grid>
    </Container>
  );
};

export default ProfessorAtividades; 