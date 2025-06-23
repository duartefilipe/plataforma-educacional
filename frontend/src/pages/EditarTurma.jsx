import React, { useState, useEffect } from 'react';
import {
    Container, Typography, Paper, TextField, Button, Box,
    FormControl, InputLabel, Select, MenuItem, CircularProgress
} from '@mui/material';
import api from '../api/axiosConfig';
import { useNavigate, useParams, useLocation } from 'react-router-dom';

const EditarTurma = () => {
    const { id } = useParams();
    const location = useLocation();
    const [nome, setNome] = useState('');
    const [anoLetivo, setAnoLetivo] = useState('');
    const [escolaId, setEscolaId] = useState('');
    const [professorId, setProfessorId] = useState('');
    const [escolas, setEscolas] = useState([]);
    const [professores, setProfessores] = useState([]);
    const [loading, setLoading] = useState(true);
    const [loadingProfessores, setLoadingProfessores] = useState(false);
    const [escolasCarregadas, setEscolasCarregadas] = useState(false);
    const [professoresCarregados, setProfessoresCarregados] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const turmaState = location.state && location.state.turma;
        const fetchEscolas = async () => {
            try {
                const escolasRes = await api.get('/escolas');
                setEscolas(escolasRes.data);
                setEscolasCarregadas(true);
            } catch (error) {
                alert('Erro ao carregar escolas.');
            }
        };
        fetchEscolas();
        if (turmaState) {
            setNome(turmaState.nome);
            setAnoLetivo(turmaState.anoLetivo);
            setTimeout(() => {
                setEscolaId(turmaState.escolaId || '');
            }, 0);
            setProfessorId(turmaState.professorId || '');
            setLoading(false);
        } else {
            const fetchTurma = async () => {
                try {
                    const turmaRes = await api.get(`/turmas/${id}`);
                    const turma = turmaRes.data;
                    setNome(turma.nome);
                    setAnoLetivo(turma.anoLetivo);
                    setTimeout(() => {
                        setEscolaId(turma.escolaId || '');
                    }, 0);
                    setProfessorId(turma.professorId || '');
                } catch (error) {
                    alert('Erro ao carregar turma.');
                }
            };
            fetchTurma().then(() => setLoading(false));
        }
    }, [id, location.state]);

    useEffect(() => {
        if (escolaId) {
            setLoadingProfessores(true);
            const fetchProfessores = async () => {
                try {
                    const response = await api.get(`/users/professores?escolaId=${escolaId}`);
                    setProfessores(response.data);
                    setProfessoresCarregados(true);
                } catch (error) {
                    alert('Erro ao carregar professores.');
                } finally {
                    setLoadingProfessores(false);
                }
            };
            fetchProfessores();
        } else {
            setProfessores([]);
            setProfessoresCarregados(false);
        }
    }, [escolaId]);

    const handleSubmit = async (event) => {
        event.preventDefault();
        try {
            await api.put(`/turmas/${id}`, { nome, anoLetivo, escolaId, professorId });
            alert('Turma atualizada com sucesso!');
            navigate('/listar-turmas');
        } catch (error) {
            alert('Erro ao atualizar turma.');
        }
    };

    if (loading) {
        return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}><CircularProgress /></Box>;
    }

    return (
        <Container maxWidth="md" sx={{ mt: 4 }}>
            <Paper elevation={3} sx={{ p: 4 }}>
                <Typography variant="h4" component="h1" gutterBottom>
                    Editar Turma
                </Typography>
                <form onSubmit={handleSubmit}>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
                        <TextField label="Nome da Turma" value={nome} onChange={(e) => setNome(e.target.value)} required fullWidth />
                        <TextField label="Ano Letivo" type="number" value={anoLetivo} onChange={(e) => setAnoLetivo(e.target.value)} required fullWidth />
                        <FormControl fullWidth required>
                            <InputLabel>Escola</InputLabel>
                            <Select
                                value={escolaId}
                                label="Escola"
                                onChange={(e) => setEscolaId(e.target.value)}
                                disabled={!escolasCarregadas}
                            >
                                {escolas.map((escola) => (
                                    <MenuItem key={escola.id} value={escola.id}>{escola.nome}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                        <FormControl fullWidth required disabled={!escolaId || loadingProfessores}>
                            <InputLabel>Professor Responsável</InputLabel>
                            <Select
                                value={professorId}
                                label="Professor Responsável"
                                onChange={(e) => setProfessorId(e.target.value)}
                                disabled={!professoresCarregados}
                            >
                                {loadingProfessores ? (
                                    <MenuItem disabled>Carregando...</MenuItem>
                                ) : (
                                    professores.map((prof) => (
                                        <MenuItem key={prof.id} value={prof.id}>{prof.nomeCompleto}</MenuItem>
                                    ))
                                )}
                            </Select>
                        </FormControl>
                        <Button type="submit" variant="contained" color="primary" sx={{ mt: 2 }}>
                            Salvar Alterações
                        </Button>
                    </Box>
                </form>
            </Paper>
        </Container>
    );
};

export default EditarTurma; 