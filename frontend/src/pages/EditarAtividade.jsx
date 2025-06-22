import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { TextField, Button, Typography, Container, CircularProgress, Alert, Paper, Box, FormControl, InputLabel, Select, MenuItem } from '@mui/material';
import axiosConfig from '../api/axiosConfig';

const EditarAtividade = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [titulo, setTitulo] = useState('');
    const [descricao, setDescricao] = useState('');
    const [tipoConteudo, setTipoConteudo] = useState('TEXTO');
    const [conteudoTexto, setConteudoTexto] = useState('');
    const [arquivo, setArquivo] = useState(null);
    const [nomeArquivoOriginal, setNomeArquivoOriginal] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        const fetchAtividade = async () => {
            try {
                setLoading(true);
                const response = await axiosConfig.get(`/atividades/${id}`);
                const atividade = response.data;
                setTitulo(atividade.titulo);
                setDescricao(atividade.descricao);
                setTipoConteudo(atividade.tipoConteudo);
                setConteudoTexto(atividade.conteudoTexto || '');
                setNomeArquivoOriginal(atividade.nomeArquivoOriginal || '');
            } catch (err) {
                setError('Erro ao carregar a atividade. ' + (err.response?.data?.message || err.message));
            } finally {
                setLoading(false);
            }
        };
        fetchAtividade();
    }, [id]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        const formData = new FormData();
        const atividadeData = { titulo, descricao, tipoConteudo, conteudoTexto };
        formData.append('atividade', new Blob([JSON.stringify(atividadeData)], { type: 'application/json' }));

        if (tipoConteudo === 'ARQUIVO_UPLOAD' && arquivo) {
            formData.append('arquivo', arquivo);
        }

        try {
            await axiosConfig.put(`/atividades/${id}`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
            setSuccess('Atividade atualizada com sucesso!');
            setTimeout(() => navigate('/professor/atividades'), 2000);
        } catch (err) {
            setError('Erro ao atualizar a atividade. ' + (err.response?.data?.message || err.message));
        }
    };

    if (loading) {
        return <CircularProgress />;
    }

    return (
        <Container component={Paper} maxWidth="md" sx={{ mt: 4, p: 4 }}>
            <Typography variant="h4" gutterBottom>Editar Atividade</Typography>
            {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
            {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}
            <form onSubmit={handleSubmit}>
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                    <TextField
                        label="Título"
                        value={titulo}
                        onChange={(e) => setTitulo(e.target.value)}
                        required
                        fullWidth
                    />
                    <TextField
                        label="Descrição"
                        value={descricao}
                        onChange={(e) => setDescricao(e.target.value)}
                        multiline
                        rows={4}
                        fullWidth
                    />
                    <FormControl fullWidth>
                        <InputLabel>Tipo de Conteúdo</InputLabel>
                        <Select value={tipoConteudo} label="Tipo de Conteúdo" onChange={(e) => setTipoConteudo(e.target.value)}>
                            <MenuItem value="TEXTO">Texto</MenuItem>
                            <MenuItem value="ARQUIVO_UPLOAD">Upload de Arquivo</MenuItem>
                        </Select>
                    </FormControl>
                    {tipoConteudo === 'TEXTO' ? (
                        <TextField
                            label="Conteúdo da Atividade"
                            value={conteudoTexto}
                            onChange={(e) => setConteudoTexto(e.target.value)}
                            multiline
                            rows={8}
                            fullWidth
                        />
                    ) : (
                        <Box>
                            <Button variant="contained" component="label">
                                {arquivo ? arquivo.name : 'Selecionar Arquivo'}
                                <input type="file" hidden onChange={(e) => setArquivo(e.target.files[0])} />
                            </Button>
                            {nomeArquivoOriginal && !arquivo && (
                                <Typography sx={{ mt: 1 }}>Arquivo atual: {nomeArquivoOriginal}</Typography>
                            )}
                        </Box>
                    )}
                    <Button type="submit" variant="contained" color="primary" sx={{ mt: 2 }}>
                        Salvar Alterações
                    </Button>
                </Box>
            </form>
        </Container>
    );
};

export default EditarAtividade; 