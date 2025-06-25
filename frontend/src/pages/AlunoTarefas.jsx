import React, { useEffect, useState, useRef } from 'react';
import {
  Container, Typography, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, CircularProgress, Alert, Button, Box, Chip, IconButton, Dialog, DialogTitle, DialogContent, DialogActions, TextField
} from '@mui/material';
import { Download, Visibility, Edit, Send } from '@mui/icons-material';
import api from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';

const AlunoTarefas = () => {
  const { user } = useAuth();
  const [tarefas, setTarefas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [openTextoModal, setOpenTextoModal] = useState(false);
  const [textoResposta, setTextoResposta] = useState('');
  const [openResponder, setOpenResponder] = useState(false);
  const [respostaTexto, setRespostaTexto] = useState('');
  const [respostaArquivo, setRespostaArquivo] = useState(null);
  const [tarefaRespondendo, setTarefaRespondendo] = useState(null);
  const fileInputRef = useRef();

  useEffect(() => {
    const fetchTarefas = async () => {
      try {
        setLoading(true);
        const res = await api.get('/aluno/tarefas');
        setTarefas(res.data);
      } catch (err) {
        setError('Erro ao carregar tarefas.');
      } finally {
        setLoading(false);
      }
    };
    fetchTarefas();
  }, []);

  const handleOpenTexto = (texto) => {
    setTextoResposta(texto);
    setOpenTextoModal(true);
  };
  const handleCloseTexto = () => {
    setOpenTextoModal(false);
    setTextoResposta('');
  };

  const handleOpenResponder = (tarefa) => {
    setTarefaRespondendo(tarefa);
    setRespostaTexto(tarefa.respostaAlunoTexto || '');
    setRespostaArquivo(null);
    setOpenResponder(true);
  };
  const handleCloseResponder = () => {
    setOpenResponder(false);
    setTarefaRespondendo(null);
    setRespostaTexto('');
    setRespostaArquivo(null);
    if (fileInputRef.current) fileInputRef.current.value = '';
  };

  const handleDownload = (tarefaId) => {
    window.open(`/api/aluno/tarefas/${tarefaId}/download-resposta`, '_blank');
  };

  const handleResponder = async () => {
    if (!tarefaRespondendo) return;
    if (!respostaTexto && !respostaArquivo) {
      setError('Preencha o texto ou selecione um arquivo para enviar.');
      return;
    }
    try {
      setLoading(true);
      const formData = new FormData();
      if (respostaTexto) formData.append('respostaTexto', respostaTexto);
      if (respostaArquivo) formData.append('respostaArquivo', respostaArquivo);
      await api.post(`/aluno/tarefas/${tarefaRespondendo.id}/responder`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      // Atualiza a lista de tarefas
      const res = await api.get('/aluno/tarefas');
      setTarefas(res.data);
      handleCloseResponder();
      setError('');
    } catch (err) {
      setError('Erro ao enviar resposta: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDENTE': return 'warning';
      case 'VISUALIZADA': return 'info';
      case 'ENTREGUE': return 'success';
      case 'AVALIADA': return 'primary';
      default: return 'default';
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString('pt-BR');
  };

  if (loading) return <CircularProgress />;
  if (error) return <Alert severity="error">{error}</Alert>;

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>Minhas Tarefas</Typography>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Atividade</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Data Designação</TableCell>
              <TableCell>Data Entrega</TableCell>
              <TableCell>Resposta</TableCell>
              <TableCell>Nota</TableCell>
              <TableCell>Ação</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {tarefas.length === 0 ? (
              <TableRow><TableCell colSpan={7}>Nenhuma tarefa encontrada.</TableCell></TableRow>
            ) : (
              tarefas.map((tarefa) => (
                <TableRow key={tarefa.id}>
                  <TableCell>{tarefa.atividadeTitulo}</TableCell>
                  <TableCell>
                    <Chip label={tarefa.status} color={getStatusColor(tarefa.status)} size="small" />
                  </TableCell>
                  <TableCell>{formatDate(tarefa.dataDesignacao)}</TableCell>
                  <TableCell>{formatDate(tarefa.dataEntrega)}</TableCell>
                  <TableCell>
                    {tarefa.respostaAlunoArquivo && (
                      <IconButton color="secondary" size="small" onClick={() => handleDownload(tarefa.id)}>
                        <Download fontSize="small" />
                      </IconButton>
                    )}
                    {tarefa.respostaAlunoTexto && (
                      <IconButton color="info" size="small" onClick={() => handleOpenTexto(tarefa.respostaAlunoTexto)}>
                        <Visibility fontSize="small" />
                      </IconButton>
                    )}
                    {!tarefa.respostaAlunoArquivo && !tarefa.respostaAlunoTexto && (
                      <span>-</span>
                    )}
                  </TableCell>
                  <TableCell>
                    {tarefa.nota ? `${tarefa.nota}/10` : '-'}
                  </TableCell>
                  <TableCell>
                    {(tarefa.status === 'PENDENTE' || tarefa.status === 'VISUALIZADA') ? (
                      <Button
                        variant="contained"
                        size="small"
                        startIcon={<Send />}
                        onClick={() => handleOpenResponder(tarefa)}
                      >
                        Responder
                      </Button>
                    ) : tarefa.status === 'ENTREGUE' ? (
                      <Button
                        variant="outlined"
                        size="small"
                        startIcon={<Edit />}
                        onClick={() => handleOpenResponder(tarefa)}
                      >
                        Editar resposta
                      </Button>
                    ) : (
                      <span>-</span>
                    )}
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Modal para visualizar resposta textual */}
      <Dialog open={openTextoModal} onClose={handleCloseTexto} maxWidth="sm" fullWidth>
        <DialogTitle>Minha Resposta</DialogTitle>
        <DialogContent>
          <pre style={{ whiteSpace: 'pre-wrap', wordBreak: 'break-word' }}>{textoResposta}</pre>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseTexto}>Fechar</Button>
        </DialogActions>
      </Dialog>

      {/* Modal para responder tarefa */}
      <Dialog open={openResponder} onClose={handleCloseResponder} maxWidth="sm" fullWidth>
        <DialogTitle>Responder Tarefa</DialogTitle>
        <DialogContent>
          <Box sx={{ mt: 2 }}>
            <TextField
              label="Resposta em texto (opcional)"
              multiline
              minRows={4}
              fullWidth
              value={respostaTexto}
              onChange={e => setRespostaTexto(e.target.value)}
              sx={{ mb: 2 }}
            />
            <Button
              variant="outlined"
              component="label"
              fullWidth
              sx={{ mb: 2 }}
            >
              Anexar Arquivo (opcional)
              <input
                ref={fileInputRef}
                type="file"
                hidden
                onChange={e => setRespostaArquivo(e.target.files[0])}
                accept=".pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx,.txt,.jpg,.jpeg,.png,.gif"
              />
            </Button>
            {respostaArquivo && (
              <Chip label={respostaArquivo.name} onDelete={() => setRespostaArquivo(null)} sx={{ mb: 2 }} />
            )}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseResponder}>Cancelar</Button>
          <Button onClick={handleResponder} variant="contained">Enviar</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default AlunoTarefas; 