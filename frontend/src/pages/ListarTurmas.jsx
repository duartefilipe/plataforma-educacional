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
  Stack
} from '@mui/material';
import api from '../api/axiosConfig';
import { useNavigate } from 'react-router-dom';

const ListarTurmas = () => {
  const [turmas, setTurmas] = useState([]);
  const [escolas, setEscolas] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

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

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja deletar esta turma?')) {
      try {
        await api.delete(`/turmas/${id}`);
        setTurmas((prev) => prev.filter((turma) => turma.id !== id));
        alert('Turma deletada com sucesso!');
      } catch (err) {
        alert('Erro ao deletar turma.');
      }
    }
  };

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
                <TableCell>Ações</TableCell>
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
                  <TableCell>
                    <Stack direction="row" spacing={1}>
                      <Button
                        variant="outlined"
                        color="primary"
                        size="small"
                        onClick={() => {
                          const turmaComIds = {
                            ...turma,
                            escolaId: turma.escolaId || turma.escola_id || null,
                            professorId: turma.professorId || turma.professor_id || null
                          };
                          navigate(`/editar-turma/${turma.id}`, { state: { turma: turmaComIds } });
                        }}
                      >
                        Editar
                      </Button>
                      <Button variant="outlined" color="error" size="small" onClick={() => handleDelete(turma.id)}>
                        Deletar
                      </Button>
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

export default ListarTurmas; 