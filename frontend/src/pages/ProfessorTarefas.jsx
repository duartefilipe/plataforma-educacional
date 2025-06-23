import React, { useEffect, useState } from 'react';
import { Container, Typography, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, CircularProgress, Alert } from '@mui/material';
import api from '../api/axiosConfig';

const ProfessorTarefas = () => {
  const [tarefas, setTarefas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchTarefas = async () => {
      try {
        setLoading(true);
        // Endpoint hipotético: /professor/tarefas
        const res = await api.get('/professor/tarefas');
        setTarefas(res.data);
      } catch (err) {
        setError('Erro ao carregar tarefas dos alunos.');
      } finally {
        setLoading(false);
      }
    };
    fetchTarefas();
  }, []);

  // Agrupar por turma e aluno (exemplo de estrutura esperada)
  // tarefas = [{ turmaNome, alunoNome, atividadeTitulo, status, dataEntrega }]

  if (loading) return <CircularProgress />;
  if (error) return <Alert severity="error">{error}</Alert>;

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>Tarefas</Typography>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Turma</TableCell>
              <TableCell>Aluno</TableCell>
              <TableCell>Atividade</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Data de Entrega</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {tarefas.length === 0 ? (
              <TableRow><TableCell colSpan={5}>Nenhuma tarefa encontrada.</TableCell></TableRow>
            ) : (
              tarefas.map((tarefa, idx) => (
                <TableRow key={idx}>
                  <TableCell>{tarefa.turmaNome}</TableCell>
                  <TableCell>{tarefa.alunoNome}</TableCell>
                  <TableCell>{tarefa.atividadeTitulo}</TableCell>
                  <TableCell>{tarefa.status}</TableCell>
                  <TableCell>{tarefa.dataEntrega}</TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Container>
  );
};

export default ProfessorTarefas; 