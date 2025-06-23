import React, { useEffect, useState } from 'react';
import api from '../api/axiosConfig';
import { useParams } from 'react-router-dom';
import { Container, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, CircularProgress, Alert, Button, Stack } from '@mui/material';

const ProfessorAtividadesTurma = () => {
  const { turmaId } = useParams();
  const [atividades, setAtividades] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchAtividades = async () => {
      try {
        setLoading(true);
        const res = await api.get(`/turmas/${turmaId}/atividades`); // Ajuste para endpoint correto
        setAtividades(Array.isArray(res.data) ? res.data : []);
      } catch (err) {
        setError('Erro ao carregar atividades.');
      } finally {
        setLoading(false);
      }
    };
    fetchAtividades();
  }, [turmaId]);

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>Atividades da Turma</Typography>
      {loading ? <CircularProgress /> : error ? <Alert severity="error">{error}</Alert> : (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Título</TableCell>
                <TableCell>Tipo</TableCell>
                <TableCell>Ações</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {atividades.map((atividade) => (
                <TableRow key={atividade.id}>
                  <TableCell>{atividade.titulo}</TableCell>
                  <TableCell>{atividade.tipoConteudo}</TableCell>
                  <TableCell>
                    <Stack direction="row" spacing={1}>
                      <Button variant="outlined" color="primary" size="small">Editar</Button>
                      <Button variant="outlined" color="success" size="small">Designar</Button>
                      <Button variant="outlined" color="error" size="small">Excluir</Button>
                    </Stack>
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

export default ProfessorAtividadesTurma; 