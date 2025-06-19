import React, { useState, useEffect } from 'react';
import { Button, Container, TextField, Typography } from '@mui/material';
import axios from 'axios';
import { useNavigate, Navigate } from 'react-router-dom';
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
      else if (user.role === 'PROFESSOR') navigate('/professor');
      else if (user.role === 'ALUNO') navigate('/aluno');
      else navigate('/');
    }
  }, [user, navigate]);

  // Redirecionar imediatamente se já estiver autenticado
  if (user) {
    if (user.role === 'ADMIN') return <Navigate to="/admin" />;
    if (user.role === 'PROFESSOR') return <Navigate to="/professor" />;
    if (user.role === 'ALUNO') return <Navigate to="/aluno" />;
  }

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      console.log('Tentando fazer login...');
      const response = await axios.post('/api/auth/login', { email, senha });
      console.log('Resposta do login:', response.data);

      const { role, email: userEmail, id } = response.data;

      if (!role) {
        alert('Credenciais inválidas.');
        return;
      }

      // Usar o contexto para fazer login
      login({ email: userEmail, role, id });
      console.log('Login realizado, contexto atualizado...');
      // O redirecionamento agora será feito pelo useEffect
    } catch (err) {
      console.error('Erro no login:', err);
      alert('Erro ao fazer login. Verifique as credenciais.');
    } finally {
      setLoading(false);
    }
  };

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
