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
  Alert
} from '@mui/material';
import api from '../api/axiosConfig';

const ListarTurmas = () => {
  const [turmas, setTurmas] = useState([]);
  const [escolas, setEscolas] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [turmasRes, escolasRes] = await Promise.all([
          api.get('/turmas'),
          api.get('/escolas')
        ]);
        
        setTurmas(turmasRes.data);
        
        const escolasMap = escolasRes.data.reduce((acc, escola) => {
          acc[escola.id] = escola.nome;
          return acc;
        }, {});
        setEscolas(escolasMap);

      } catch (err) {
        setError('Falha ao carregar dados.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>
        Turmas Cadastradas
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
                <TableCell>Nome da Turma</TableCell>
                <TableCell>Ano Letivo</TableCell>
                <TableCell>Escola</TableCell>
                <TableCell>Professor Responsável</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {turmas.map((turma) => (
                <TableRow key={turma.id}>
                  <TableCell>{turma.id}</TableCell>
                  <TableCell>{turma.nome}</TableCell>
                  <TableCell>{turma.anoLetivo}</TableCell>
                  <TableCell>{turma.nomeEscola}</TableCell>
                  <TableCell>{turma.professorNome || 'N/A'}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </Container>
  );
};

export default ListarTurmas; 