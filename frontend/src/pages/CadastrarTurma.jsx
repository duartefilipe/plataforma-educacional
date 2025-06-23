import React, { useState, useEffect } from 'react';
import {
    Container, Typography, Paper, TextField, Button, Box,
    FormControl, InputLabel, Select, MenuItem, CircularProgress
} from '@mui/material';
import api from '../api/axiosConfig';
import { useNavigate } from 'react-router-dom';

const CadastrarTurma = () => {
    const [nome, setNome] = useState('');
    const [anoLetivo, setAnoLetivo] = useState(new Date().getFullYear());
    const [escolaId, setEscolaId] = useState('');
    const [professorId, setProfessorId] = useState('');
    const [turno, setTurno] = useState('');
    
    const [escolas, setEscolas] = useState([]);
    const [professores, setProfessores] = useState([]);
    
    const [loadingEscolas, setLoadingEscolas] = useState(true);
    const [loadingProfessores, setLoadingProfessores] = useState(false);
    
    const navigate = useNavigate();

    // Efeito para buscar as escolas
    useEffect(() => {
        const fetchEscolas = async () => {
            try {
                const response = await api.get('/escolas');
                setEscolas(response.data);
            } catch (error) {
                console.error("Erro ao buscar escolas:", error);
                alert("Não foi possível carregar as escolas.");
            } finally {
                setLoadingEscolas(false);
            }
        };
        fetchEscolas();
    }, []);

    // Efeito para buscar professores quando uma escola é selecionada
    useEffect(() => {
        if (escolaId) {
            setLoadingProfessores(true);
            setProfessorId(''); // Reseta a seleção de professor
            const fetchProfessores = async () => {
                try {
                    const response = await api.get(`/users/professores?escolaId=${escolaId}`);
                    setProfessores(response.data);
                } catch (error) {
                    console.error("Erro ao buscar professores:", error);
                    alert("Não foi possível carregar os professores para esta escola.");
                } finally {
                    setLoadingProfessores(false);
                }
            };
            fetchProfessores();
        } else {
            setProfessores([]); // Limpa a lista se nenhuma escola estiver selecionada
        }
    }, [escolaId]);

    const handleSubmit = async (event) => {
        event.preventDefault();
        if (!escolaId || !professorId) {
            alert('Por favor, selecione a escola e o professor responsável.');
            return;
        }
        try {
            await api.post('/turmas', { nome, anoLetivo, escolaId, professorId, turno });
            alert('Turma cadastrada com sucesso!');
            navigate('/listar-turmas');
        } catch (error) {
            console.error('Erro ao cadastrar turma:', error);
            alert('Falha ao cadastrar turma.');
        }
    };

    if (loadingEscolas) {
        return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}><CircularProgress /></Box>;
    }

    return (
        <Container maxWidth="md" sx={{ mt: 4 }}>
            <Paper elevation={3} sx={{ p: 4 }}>
                <Typography variant="h4" component="h1" gutterBottom>
                    Cadastrar Nova Turma
                </Typography>
                <form onSubmit={handleSubmit}>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
                        <TextField label="Nome da Turma" value={nome} onChange={(e) => setNome(e.target.value)} required fullWidth />
                        <TextField label="Ano Letivo" type="number" value={anoLetivo} onChange={(e) => setAnoLetivo(e.target.value)} required fullWidth />
                        
                        <FormControl fullWidth required>
                            <InputLabel>Escola</InputLabel>
                            <Select value={escolaId} label="Escola" onChange={(e) => setEscolaId(e.target.value)}>
                                {escolas.map((escola) => (
                                    <MenuItem key={escola.id} value={escola.id}>{escola.nome}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                        
                        <FormControl fullWidth required disabled={!escolaId || loadingProfessores}>
                            <InputLabel>Professor Responsável</InputLabel>
                            <Select value={professorId} label="Professor Responsável" onChange={(e) => setProfessorId(e.target.value)}>
                                {loadingProfessores ? (
                                    <MenuItem disabled>Carregando...</MenuItem>
                                ) : (
                                    professores.map((prof) => (
                                        <MenuItem key={prof.id} value={prof.id}>{prof.nomeCompleto}</MenuItem>
                                    ))
                                )}
                            </Select>
                        </FormControl>
                        
                        <FormControl fullWidth required>
                            <InputLabel>Turno</InputLabel>
                            <Select value={turno} label="Turno" onChange={e => setTurno(e.target.value)}>
                                <MenuItem value="MANHA">Manhã</MenuItem>
                                <MenuItem value="TARDE">Tarde</MenuItem>
                                <MenuItem value="INTEGRAL">Integral</MenuItem>
                            </Select>
                        </FormControl>
                        
                        <Button type="submit" variant="contained" color="primary" sx={{ mt: 2 }}>
                            Cadastrar
                        </Button>
                    </Box>
                </form>
            </Paper>
        </Container>
    );
};

export default CadastrarTurma; 