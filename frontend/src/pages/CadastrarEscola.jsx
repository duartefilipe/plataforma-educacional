import React, { useState } from 'react';
import { TextField, Button, Container, Typography, Paper } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';

const CadastrarEscola = () => {
    const navigate = useNavigate();
    const [nome, setNome] = useState('');
    const [endereco, setEndereco] = useState('');
    const [contatoTelefone, setContatoTelefone] = useState('');
    const [contatoEmail, setContatoEmail] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (!nome) {
            setError('O nome da escola é obrigatório.');
            return;
        }

        const escolaData = {
            nome,
            endereco,
            contatoTelefone,
            contatoEmail
        };

        try {
            await api.post('/escolas', escolaData);
            alert('Escola cadastrada com sucesso!');
            navigate('/admin'); // Ou para a lista de escolas
        } catch (err) {
            setError(err.response?.data?.message || 'Erro ao cadastrar escola. Tente novamente.');
            alert(err.response?.data?.message || 'Erro ao cadastrar escola. Tente novamente.');
        }
    };

    return (
        <Container maxWidth="sm" style={{ marginTop: '2rem' }}>
            <Paper elevation={3} style={{ padding: '2rem' }}>
                <Typography variant="h4" gutterBottom>
                    Cadastrar Nova Escola
                </Typography>
                <form onSubmit={handleSubmit}>
                    <TextField
                        label="Nome da Escola"
                        variant="outlined"
                        fullWidth
                        required
                        value={nome}
                        onChange={(e) => setNome(e.target.value)}
                        margin="normal"
                    />
                    <TextField
                        label="Endereço"
                        variant="outlined"
                        fullWidth
                        value={endereco}
                        onChange={(e) => setEndereco(e.target.value)}
                        margin="normal"
                    />
                    <TextField
                        label="Telefone de Contato"
                        variant="outlined"
                        fullWidth
                        value={contatoTelefone}
                        onChange={(e) => setContatoTelefone(e.target.value)}
                        margin="normal"
                    />
                    <TextField
                        label="Email de Contato"
                        variant="outlined"
                        fullWidth
                        type="email"
                        value={contatoEmail}
                        onChange={(e) => setContatoEmail(e.target.value)}
                        margin="normal"
                    />
                    {error && <Typography color="error" style={{ marginBottom: '1rem' }}>{error}</Typography>}
                    <Button type="submit" variant="contained" color="primary" fullWidth size="large">
                        Cadastrar Escola
                    </Button>
                </form>
            </Paper>
        </Container>
    );
};

export default CadastrarEscola; 