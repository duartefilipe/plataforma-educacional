import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Container, Typography, Paper, CircularProgress, Box } from '@mui/material';
import EscolaForm from '../components/EscolaForm';
import api from '../api/axiosConfig';

const EditarEscola = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [escola, setEscola] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchEscola = async () => {
            try {
                const response = await api.get(`/escolas/${id}`);
                setEscola(response.data);
            } catch (error) {
                console.error('Erro ao buscar escola:', error);
            } finally {
                setLoading(false);
            }
        };
        fetchEscola();
    }, [id]);

    const handleUpdateEscola = async (escolaData) => {
        try {
            await api.put(`/escolas/${id}`, escolaData);
            alert('Escola atualizada com sucesso!');
            navigate('/listar-escolas');
        } catch (error) {
            console.error('Erro ao atualizar escola:', error);
            alert('Falha ao atualizar escola.');
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
        <Container maxWidth="md" sx={{ mt: 4 }}>
            <Paper elevation={3} sx={{ p: 4 }}>
                <Typography variant="h4" component="h1" gutterBottom>
                    Editar Escola
                </Typography>
                {escola ? (
                    <EscolaForm onSubmit={handleUpdateEscola} initialData={escola} />
                ) : (
                    <Typography>Escola não encontrada.</Typography>
                )}
            </Paper>
        </Container>
    );
};

export default EditarEscola; 