import React from 'react';
import { Container, Typography, Paper } from '@mui/material';
import UserForm from '../components/UserForm';
import api from '../api/axiosConfig';
import { useNavigate, useLocation } from 'react-router-dom';

const CadastrarUsuario = () => {
    const navigate = useNavigate();
    const location = useLocation();

    // Captura o perfil da query string (?role=PROFESSOR)
    const params = new URLSearchParams(location.search);
    const defaultRole = params.get('role');

    const handleCreateUser = async (userData) => {
        try {
            await api.post('/users/criar', userData);
            alert('Usuário cadastrado com sucesso!');
            navigate('/listar-usuarios');
        } catch (error) {
            console.error('Erro ao cadastrar usuário:', error);
            alert('Falha ao cadastrar usuário. Verifique o console para mais detalhes.');
        }
    };

    return (
        <Container maxWidth="md" sx={{ mt: 4 }}>
            <Paper elevation={3} sx={{ p: 4 }}>
                <Typography variant="h4" component="h1" gutterBottom>
                    Cadastrar Novo Usuário
                </Typography>
                <UserForm onSubmit={handleCreateUser} isEdit={false} defaultRole={defaultRole} />
            </Paper>
        </Container>
    );
};

export default CadastrarUsuario; 