import React, { useState, useEffect } from 'react';
import { TextField, Button, Select, MenuItem, FormControl, InputLabel, Switch, FormControlLabel, Box, CircularProgress } from '@mui/material';
import api from '../api/axiosConfig';

const UserForm = ({ initialData, onSubmit, isEdit = false }) => {
    // Estados do formulário
    const [nomeCompleto, setNomeCompleto] = useState('');
    const [email, setEmail] = useState('');
    const [senha, setSenha] = useState('');
    const [role, setRole] = useState('ALUNO');
    const [ativo, setAtivo] = useState(true);
    const [escolaId, setEscolaId] = useState('');
    const [turmaId, setTurmaId] = useState('');

    // Estados para dados de suporte (escolas, turmas)
    const [escolas, setEscolas] = useState([]);
    const [turmas, setTurmas] = useState([]);
    const [loadingTurmas, setLoadingTurmas] = useState(false);

    // Efeito para popular o formulário com dados iniciais (na edição)
    useEffect(() => {
        if (isEdit && initialData) {
            setNomeCompleto(initialData.nomeCompleto || '');
            setEmail(initialData.email || '');
            setRole(initialData.role || 'ALUNO');
            setAtivo(initialData.ativo !== undefined ? initialData.ativo : true);
            setEscolaId(initialData.escolaId || '');
            setTurmaId(initialData.turmaId || '');
        }
    }, [isEdit, initialData]);

    // Efeito para buscar escolas quando o perfil for Professor ou Aluno
    useEffect(() => {
        if (role === 'PROFESSOR' || role === 'ALUNO') {
            api.get('/escolas')
                .then(response => setEscolas(response.data))
                .catch(error => console.error("Erro ao buscar escolas", error));
        }
    }, [role]);

    // Efeito para buscar as turmas da escola do aluno (na edição)
    useEffect(() => {
        if (isEdit && role === 'ALUNO' && escolaId) {
            setLoadingTurmas(true);
            api.get(`/turmas?escolaId=${escolaId}`)
                .then(response => {
                    setTurmas(response.data);
                })
                .catch(error => console.error("Erro ao buscar turmas da escola", error))
                .finally(() => setLoadingTurmas(false));
        } else {
            setTurmas([]);
        }
    }, [isEdit, role, escolaId]);


    const handleSubmit = (e) => {
        e.preventDefault();
        const userData = { nomeCompleto, email, role, ativo, senha, escolaId };
        
        if (role === 'ALUNO') {
            userData.turmaId = turmaId || null; // Envia null se desvinculado
        }
        
        // Remove a senha do payload se não for preenchida na edição
        if (isEdit && !senha) {
            delete userData.senha;
        }

        onSubmit(userData);
    };

    const showEscolaSelect = role === 'PROFESSOR' || role === 'ALUNO';

    return (
        <form onSubmit={handleSubmit}>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
                <TextField label="Nome Completo" value={nomeCompleto} onChange={(e) => setNomeCompleto(e.target.value)} required fullWidth />
                <TextField label="Email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required fullWidth />
                <TextField label="Senha" type="password" value={senha} onChange={(e) => setSenha(e.target.value)} placeholder={isEdit ? "Deixe em branco para não alterar" : ""} required={!isEdit} fullWidth />

                {!isEdit && (
                    <FormControl fullWidth required>
                        <InputLabel>Perfil</InputLabel>
                        <Select value={role} label="Perfil" onChange={(e) => setRole(e.target.value)}>
                            <MenuItem value="ALUNO">Aluno</MenuItem>
                            <MenuItem value="PROFESSOR">Professor</MenuItem>
                            <MenuItem value="ADMIN">Admin</MenuItem>
                        </Select>
                    </FormControl>
                )}

                {showEscolaSelect && (
                    <FormControl fullWidth required>
                        <InputLabel>Escola</InputLabel>
                        <Select value={escolaId} label="Escola" onChange={(e) => setEscolaId(e.target.value)}>
                             {escolas.map((escola) => (
                                <MenuItem key={escola.id} value={escola.id}>{escola.nome}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                )}

                {isEdit && role === 'ALUNO' && (
                    <FormControl fullWidth>
                        <InputLabel>Turma</InputLabel>
                        <Select value={turmaId} label="Turma" onChange={(e) => setTurmaId(e.target.value)} disabled={loadingTurmas}>
                            <MenuItem value=""><em>Nenhuma / Desvincular</em></MenuItem>
                            {loadingTurmas ? 
                                <Box sx={{ display: 'flex', justifyContent: 'center', p: 2 }}><CircularProgress size={24} /></Box> :
                                turmas.map((turma) => (
                                    <MenuItem key={turma.id} value={turma.id}>{turma.nome} - {turma.anoLetivo}</MenuItem>
                                ))
                            }
                        </Select>
                    </FormControl>
                )}

                {isEdit && (
                    <FormControlLabel control={<Switch checked={ativo} onChange={(e) => setAtivo(e.target.checked)} />} label="Ativo" />
                )}

                <Button type="submit" variant="contained" color="primary" sx={{ mt: 2 }}>
                    {isEdit ? 'Salvar Alterações' : 'Cadastrar'}
                </Button>
            </Box>
        </form>
    );
};

export default UserForm;