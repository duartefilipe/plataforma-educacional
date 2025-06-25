import React, { useEffect, useState } from 'react';
import { 
  Container, 
  Typography, 
  Paper, 
  Table, 
  TableBody, 
  TableCell, 
  TableContainer, 
  TableHead, 
  TableRow, 
  CircularProgress, 
  Alert,
  Button,
  Box,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Grid,
  Card,
  CardContent,
  Chip,
  IconButton
} from '@mui/material';
import { Add, School, Group, Assignment, Download, Edit, Save, Visibility } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';

const ProfessorTarefas = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [tarefas, setTarefas] = useState([]);
  const [escolas, setEscolas] = useState([]);
  const [turmas, setTurmas] = useState([]);
  const [atividades, setAtividades] = useState([]);
  const [selectedEscola, setSelectedEscola] = useState('');
  const [selectedTurma, setSelectedTurma] = useState('');
  const [selectedAtividade, setSelectedAtividade] = useState('');
  const [loading, setLoading] = useState(true);
  const [loadingTarefas, setLoadingTarefas] = useState(false);
  const [error, setError] = useState('');
  const [editNotaId, setEditNotaId] = useState(null);
  const [notaEdit, setNotaEdit] = useState('');
  const [openTextoModal, setOpenTextoModal] = useState(false);
  const [textoResposta, setTextoResposta] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [escolasRes, atividadesRes] = await Promise.all([
          api.get('/users/me/escolas'),
          api.get('/atividades/professor/me')
        ]);
        setEscolas(escolasRes.data);
        setAtividades(atividadesRes.data);
      } catch (err) {
        setError('Erro ao carregar dados iniciais.');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  useEffect(() => {
    if (selectedEscola) {
      const fetchTurmas = async () => {
        try {
          const res = await api.get(`/turmas?escolaId=${selectedEscola}`);
          setTurmas(res.data);
          setSelectedTurma('');
        } catch (err) {
          setError('Erro ao carregar turmas da escola.');
        }
      };
      fetchTurmas();
    } else {
      setTurmas([]);
      setSelectedTurma('');
    }
  }, [selectedEscola]);

  useEffect(() => {
    if (selectedTurma) {
      fetchTarefasTurma();
    } else {
      setTarefas([]);
    }
  }, [selectedTurma]);

  const fetchTarefasTurma = async () => {
    if (!selectedTurma) return;
    
    try {
      setLoadingTarefas(true);
      const res = await api.get(`/professor/tarefas/turma/${selectedTurma}`);
      setTarefas(res.data);
    } catch (err) {
      setError('Erro ao carregar tarefas da turma.');
    } finally {
      setLoadingTarefas(false);
    }
  };

  const handleDesignarTarefa = async () => {
    if (!selectedAtividade || !selectedTurma) {
      setError('Por favor, selecione uma atividade e uma turma.');
      return;
    }

    try {
      setLoadingTarefas(true);
      await api.post(`/atividades/${selectedAtividade}/designar-turma`, {
        turmaId: selectedTurma
      });
      
      // Recarregar tarefas da turma
      await fetchTarefasTurma();
      
      // Limpar seleção
      setSelectedAtividade('');
      
      setError('');
    } catch (err) {
      setError('Erro ao designar tarefa: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoadingTarefas(false);
    }
  };

  const handleDownload = (tarefaId) => {
    window.open(`/api/professor/tarefas/${tarefaId}/download-resposta`, '_blank');
  };

  const handleEditNota = (tarefa) => {
    setEditNotaId(tarefa.id);
    setNotaEdit(tarefa.nota || '');
  };

  const handleSaveNota = async (tarefaId) => {
    if (
      notaEdit === '' ||
      isNaN(Number(notaEdit)) ||
      Number(notaEdit) < 0 ||
      Number(notaEdit) > 10
    ) {
      setError('Nota inválida. Digite um valor entre 0 e 10.');
      return;
    }
    try {
      await api.put(`/professor/tarefas/${tarefaId}/nota`, null, { params: { nota: notaEdit } });
      // Atualiza localmente
      setTarefas(tarefas.map(t => t.id === tarefaId ? { ...t, nota: notaEdit } : t));
      setEditNotaId(null);
    } catch (err) {
      setError('Erro ao salvar nota: ' + (err.response?.data?.message || err.message));
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

  const handleOpenTexto = (texto) => {
    setTextoResposta(texto);
    setOpenTextoModal(true);
  };

  const handleCloseTexto = () => {
    setOpenTextoModal(false);
    setTextoResposta('');
  };

  if (loading) {
    return (
      <Container sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '50vh' }}>
        <CircularProgress />
      </Container>
    );
  }

  return (
    <Container maxWidth="xl" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
        <Assignment /> Gerenciar Tarefas
      </Typography>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {/* Seção de Designação de Tarefas */}
      <Card sx={{ mb: 4 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Add /> Designar Nova Tarefa
          </Typography>
          
          <Grid container spacing={2} alignItems="center">
            <Grid item xs={12} md={3}>
              <FormControl fullWidth size="small">
                <InputLabel>Escola</InputLabel>
                <Select
                  value={selectedEscola}
                  label="Escola"
                  onChange={(e) => setSelectedEscola(e.target.value)}
                >
                  {escolas.map((escola) => (
                    <MenuItem key={escola.id} value={escola.id}>
                      {escola.nome}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            
            <Grid item xs={12} md={3}>
              <FormControl fullWidth size="small">
                <InputLabel>Turma</InputLabel>
                <Select
                  value={selectedTurma}
                  label="Turma"
                  onChange={(e) => setSelectedTurma(e.target.value)}
                  disabled={!selectedEscola}
                >
                  {turmas.map((turma) => (
                    <MenuItem key={turma.id} value={turma.id}>
                      {turma.nome} - {turma.anoLetivo}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            
            <Grid item xs={12} md={4}>
              <FormControl fullWidth size="small">
                <InputLabel>Atividade</InputLabel>
                <Select
                  value={selectedAtividade}
                  label="Atividade"
                  onChange={(e) => setSelectedAtividade(e.target.value)}
                >
                  {atividades.map((atividade) => (
                    <MenuItem key={atividade.id} value={atividade.id}>
                      {atividade.titulo}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            
            <Grid item xs={12} md={2}>
              <Button
                variant="contained"
                fullWidth
                onClick={handleDesignarTarefa}
                disabled={!selectedAtividade || !selectedTurma || loadingTarefas}
                size="small"
              >
                {loadingTarefas ? <CircularProgress size={20} /> : 'Designar'}
              </Button>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {/* Seção de Visualização de Tarefas */}
      {selectedTurma && (
        <Card>
          <CardContent>
            <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <Group /> Tarefas da Turma Selecionada
            </Typography>
            
            {loadingTarefas ? (
              <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
                <CircularProgress />
              </Box>
            ) : (
              <TableContainer component={Paper} variant="outlined">
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Aluno</TableCell>
                      <TableCell>Atividade</TableCell>
                      <TableCell>Status</TableCell>
                      <TableCell>Data Designação</TableCell>
                      <TableCell>Data Entrega</TableCell>
                      <TableCell>Resposta</TableCell>
                      <TableCell>Nota</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {tarefas.length === 0 ? (
                      <TableRow>
                        <TableCell colSpan={7} align="center">
                          Nenhuma tarefa encontrada para esta turma.
                        </TableCell>
                      </TableRow>
                    ) : (
                      tarefas.map((tarefa) => (
                        <TableRow key={tarefa.id} hover>
                          <TableCell>{tarefa.alunoNome}</TableCell>
                          <TableCell>{tarefa.atividadeTitulo}</TableCell>
                          <TableCell>
                            <Chip 
                              label={tarefa.status} 
                              color={getStatusColor(tarefa.status)}
                              size="small"
                            />
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
                            {editNotaId === tarefa.id ? (
                              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                <input
                                  type="number"
                                  min="0"
                                  max="10"
                                  step="0.1"
                                  value={notaEdit}
                                  onChange={e => setNotaEdit(e.target.value)}
                                  style={{ width: 60 }}
                                />
                                <IconButton color="primary" size="small" onClick={() => handleSaveNota(tarefa.id)}>
                                  <Save fontSize="small" />
                                </IconButton>
                              </Box>
                            ) : (
                              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                {tarefa.nota ? `${tarefa.nota}/10` : '-'}
                                <IconButton color="primary" size="small" onClick={() => handleEditNota(tarefa)}>
                                  <Edit fontSize="small" />
                                </IconButton>
                              </Box>
                            )}
                          </TableCell>
                        </TableRow>
                      ))
                    )}
                  </TableBody>
                </Table>
              </TableContainer>
            )}
          </CardContent>
        </Card>
      )}

      {/* Instruções */}
      {!selectedTurma && (
        <Card>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Como usar:
            </Typography>
            <Typography variant="body2" color="text.secondary">
              1. Selecione uma escola vinculada ao seu perfil
            </Typography>
            <Typography variant="body2" color="text.secondary">
              2. Escolha uma turma da escola selecionada
            </Typography>
            <Typography variant="body2" color="text.secondary">
              3. Selecione uma atividade para designar
            </Typography>
            <Typography variant="body2" color="text.secondary">
              4. Clique em "Designar" para criar tarefas para todos os alunos da turma
            </Typography>
            <Typography variant="body2" color="text.secondary">
              5. Visualize o status das tarefas na tabela abaixo
            </Typography>
          </CardContent>
        </Card>
      )}

      <Dialog open={openTextoModal} onClose={handleCloseTexto} maxWidth="sm" fullWidth>
        <DialogTitle>Resposta do Aluno</DialogTitle>
        <DialogContent>
          <pre style={{ whiteSpace: 'pre-wrap', wordBreak: 'break-word' }}>{textoResposta}</pre>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseTexto}>Fechar</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default ProfessorTarefas; 