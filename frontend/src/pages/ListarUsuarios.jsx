import React, { useEffect, useState } from 'react';
import api from '../api/axiosConfig';
import {
    Container, Typography, Paper, Table, TableBody, TableCell,
    TableContainer, TableHead, TableRow, IconButton, Box, CircularProgress
} from '@mui/material';
import { Edit, Delete } from '@mui/icons-material';
import { useNavigate, useLocation } from 'react-router-dom';

const ListarUsuarios = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();
    const location = useLocation();

    // Captura o perfil da query string (?role=PROFESSOR)
    const params = new URLSearchParams(location.search);
    let filterRole = params.get('role');
    // Se não houver filtro, assume ADMIN por padrão (aba Usuários)
    if (!filterRole) filterRole = 'ADMIN';

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                setLoading(true);
                const response = await api.get('/users/all');
                let data = response.data;
                if (filterRole) {
                    data = data.filter(u => u.role === filterRole);
                }
                setUsers(data);
            } catch (error) {
                console.error("Erro ao buscar usuários:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchUsers();
    }, [filterRole]);

    const handleEdit = (id) => {
        navigate(`/editar-usuario/${id}`);
    };

    const handleDelete = async (id) => {
        if (window.confirm('Tem certeza que deseja deletar este usuário?')) {
            try {
                await api.delete(`/users/${id}`);
                setUsers(users.filter(user => user.id !== id));
            } catch (error) {
                console.error("Erro ao deletar usuário:", error);
                alert('Falha ao deletar usuário.');
            }
        }
    };

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Container maxWidth="lg" sx={{ mt: 4 }}>
            <Typography variant="h4" component="h1" gutterBottom>
                Lista de Usuários{filterRole ? ` - ${filterRole.charAt(0) + filterRole.slice(1).toLowerCase()}` : ''}
            </Typography>
            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>ID</TableCell>
                            <TableCell>Nome Completo</TableCell>
                            <TableCell>Email</TableCell>
                            <TableCell>Ativo</TableCell>
                            <TableCell>Ações</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {users.map((user) => (
                            <TableRow key={user.id}>
                                <TableCell>{user.id}</TableCell>
                                <TableCell>{user.nomeCompleto}</TableCell>
                                <TableCell>{user.email}</TableCell>
                                <TableCell>{user.ativo ? 'Sim' : 'Não'}</TableCell>
                                <TableCell>
                                    <IconButton onClick={() => handleEdit(user.id)} color="primary">
                                        <Edit />
                                    </IconButton>
                                    <IconButton onClick={() => handleDelete(user.id)} color="error">
                                        <Delete />
                                    </IconButton>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Container>
    );
};

export default ListarUsuarios; 