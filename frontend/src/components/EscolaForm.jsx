import React, { useState, useEffect } from 'react';
import { TextField, Button, Box } from '@mui/material';

const EscolaForm = ({ onSubmit, initialData = null }) => {
    const [nome, setNome] = useState('');
    const [emailContato, setEmailContato] = useState('');
    const [telefone, setTelefone] = useState('');

    useEffect(() => {
        if (initialData) {
            setNome(initialData.nome || '');
            setEmailContato(initialData.emailContato || '');
            setTelefone(initialData.telefone || '');
        }
    }, [initialData]);

    const handleSubmit = (event) => {
        event.preventDefault();
        onSubmit({ nome, emailContato, telefone });
    };

    return (
        <form onSubmit={handleSubmit}>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
                <TextField
                    label="Nome da Escola"
                    value={nome}
                    onChange={(e) => setNome(e.target.value)}
                    required
                    fullWidth
                />
                <TextField
                    label="Email de Contato"
                    type="email"
                    value={emailContato}
                    onChange={(e) => setEmailContato(e.target.value)}
                    fullWidth
                />
                <TextField
                    label="Telefone"
                    value={telefone}
                    onChange={(e) => setTelefone(e.target.value)}
                    fullWidth
                />
                <Button type="submit" variant="contained" color="primary" sx={{ mt: 2 }}>
                    {initialData ? 'Salvar Alterações' : 'Cadastrar'}
                </Button>
            </Box>
        </form>
    );
};

export default EscolaForm; 