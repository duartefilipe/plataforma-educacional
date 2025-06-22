import React, { useState, useEffect } from 'react';
import { TextField, Button, Typography, Paper, Container, MenuItem, Select, InputLabel, FormControl } from '@mui/material';
import api from '../api/axiosConfig';

const UserForm = ({ role, title, onSubmit, initialData }) => {
  const [formData, setFormData] = useState({
    nomeCompleto: '',
    email: '',
    senha: '',
    role: role,
    escolaId: ''
  });
  const [escolas, setEscolas] = useState([]);
  const [error, setError] = useState('');
  const isEdit = !!initialData;

  useEffect(() => {
    if (role === 'PROFESSOR') {
      const fetchEscolas = async () => {
        try {
          const response = await api.get('/escolas');
          setEscolas(response.data);
        } catch (err) {
          console.error('Erro ao buscar escolas', err);
        }
      };
      fetchEscolas();
    }
  }, [role]);

  useEffect(() => {
    if (initialData) {
      setFormData({
        nomeCompleto: initialData.nomeCompleto || '',
        email: initialData.email || '',
        senha: '',
        role: initialData.role || role,
        escolaId: initialData.escolaId || '' 
      });
    }
  }, [initialData, role]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!formData.nomeCompleto || !formData.email || (!isEdit && !formData.senha)) {
      setError('Por favor, preencha todos os campos obrigatórios.');
      return;
    }
    if (role === 'PROFESSOR' && !formData.escolaId) {
      setError('Por favor, selecione uma escola para o professor.');
      return;
    }
    setError('');
    onSubmit(formData);
  };

  return (
    <Container maxWidth="sm" style={{ marginTop: '2rem' }}>
      <Paper elevation={3} style={{ padding: '2rem' }}>
        <Typography variant="h4" component="h1" gutterBottom>
          {title}
        </Typography>
        <form onSubmit={handleSubmit}>
          <TextField
            label="Nome Completo"
            name="nomeCompleto"
            value={formData.nomeCompleto}
            onChange={handleChange}
            fullWidth
            margin="normal"
            required
          />
          <TextField
            label="Email"
            name="email"
            type="email"
            value={formData.email}
            onChange={handleChange}
            fullWidth
            margin="normal"
            required
          />
          <TextField
            label={isEdit ? "Nova Senha (deixe em branco para não alterar)" : "Senha"}
            name="senha"
            type="password"
            value={formData.senha}
            onChange={handleChange}
            fullWidth
            margin="normal"
            required={!isEdit}
          />

          {role === 'PROFESSOR' && (
            <FormControl fullWidth margin="normal" required>
              <InputLabel id="escola-select-label">Escola</InputLabel>
              <Select
                labelId="escola-select-label"
                name="escolaId"
                value={formData.escolaId}
                onChange={handleChange}
                label="Escola"
              >
                <MenuItem value="">
                  <em>Selecione uma escola</em>
                </MenuItem>
                {escolas.map((escola) => (
                  <MenuItem key={escola.id} value={escola.id}>
                    {escola.nome}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          )}

          {error && <Typography color="error">{error}</Typography>}
          
          <Button type="submit" variant="contained" color="primary" fullWidth style={{ marginTop: '1rem' }}>
            {isEdit ? 'Atualizar' : 'Criar'}
          </Button>
        </form>
      </Paper>
    </Container>
  );
};

export default UserForm; 