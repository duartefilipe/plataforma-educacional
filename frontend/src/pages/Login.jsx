import React, { useState } from 'react';
import { Button, Container, TextField, Typography } from '@mui/material';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function Login() {
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('/api/auth/login', { email, senha });

      const { token, role } = response.data;

      if (!token || !role) {
        alert('Credenciais inválidas.');
        return;
      }

      localStorage.setItem('token', token);
      alert('Login realizado com sucesso!');

      if (role === 'ADMIN') navigate('/admin');
      else if (role === 'PROFESSOR') navigate('/professor');
      else if (role === 'ALUNO') navigate('/aluno');
      else navigate('/');
    } catch (err) {
      alert('Erro ao fazer login. Verifique as credenciais.');
    }

  };

  return (
    <Container maxWidth="sm" style={{ marginTop: '4rem' }}>
      <Typography variant="h4" gutterBottom>Login</Typography>
      <form onSubmit={handleLogin}>
        <TextField label="Email" fullWidth margin="normal" value={email} onChange={e => setEmail(e.target.value)} />
        <TextField label="Senha" type="password" fullWidth margin="normal" value={senha} onChange={e => setSenha(e.target.value)} />
        <Button variant="contained" color="primary" fullWidth type="submit">Entrar</Button>
      </form>
    </Container>
  );
}

export default Login;
