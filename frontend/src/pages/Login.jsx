import React, { useState, useEffect } from 'react';
import { Button, Container, TextField, Typography } from '@mui/material';
import api from '../api/axiosConfig';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Login() {
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { login, user } = useAuth();

  useEffect(() => {
    if (user) {
      if (user.role === 'ADMIN') navigate('/admin');
      if (user.role === 'PROFESSOR') navigate('/professor');
      // Adicionar outras roles se necessário
    }
  }, [user, navigate]);

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      // A URL da API é fixa para localhost para o ambiente de desenvolvimento
      const response = await api.post('/auth/login', { email, senha });
      login(response.data);
    } catch (err) {
      alert('Erro ao fazer login. Verifique as credenciais.');
    } finally {
      setLoading(false);
    }
  };

  if (user) return null; // Não renderiza nada se já estiver logado (e esperando redirecionamento)

  return (
    <Container maxWidth="sm" style={{ marginTop: '4rem' }}>
      <Typography variant="h4" gutterBottom>Login</Typography>
      <form onSubmit={handleLogin}>
        <TextField 
          label="Email" 
          fullWidth 
          margin="normal" 
          value={email} 
          onChange={e => setEmail(e.target.value)}
          disabled={loading}
        />
        <TextField 
          label="Senha" 
          type="password" 
          fullWidth 
          margin="normal" 
          value={senha} 
          onChange={e => setSenha(e.target.value)}
          disabled={loading}
        />
        <Button 
          variant="contained" 
          color="primary" 
          fullWidth 
          type="submit"
          disabled={loading}
        >
          {loading ? 'Entrando...' : 'Entrar'}
        </Button>
      </form>
    </Container>
  );
}

export default Login; 