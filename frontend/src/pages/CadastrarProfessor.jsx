import React, { useState } from 'react';
import { Button, Container, TextField, Typography, Alert } from '@mui/material';
import axios from 'axios';
import Navbar from '../components/Navbar';

export default function CadastrarProfessor() {
  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [sucesso, setSucesso] = useState(false);
  const [erro, setErro] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErro('');
    setSucesso(false);
    console.log('Enviando cadastro de professor:', { nome, email, senha });
    try {
      await axios.post('/api/professores', {
        nomeCompleto: nome,
        email,
        senha
      });
      setSucesso(true);
      setNome('');
      setEmail('');
      setSenha('');
    } catch (err) {
      setErro(err.response?.data || 'Erro ao cadastrar professor.');
      console.error('Erro ao cadastrar professor:', err);
    }
  };

  return (
    <>
      <Navbar />
      <Container maxWidth="sm" style={{ marginTop: '4rem' }}>
        <Typography variant="h5" gutterBottom>Cadastrar Professor</Typography>
        {sucesso && <Alert severity="success">Professor cadastrado com sucesso!</Alert>}
        {erro && <Alert severity="error">{erro}</Alert>}
        <form onSubmit={handleSubmit}>
          <TextField label="Nome" fullWidth margin="normal" value={nome} onChange={e => setNome(e.target.value)} required />
          <TextField label="Email" fullWidth margin="normal" value={email} onChange={e => setEmail(e.target.value)} required />
          <TextField label="Senha" type="password" fullWidth margin="normal" value={senha} onChange={e => setSenha(e.target.value)} required />
          <Button variant="contained" color="primary" fullWidth type="submit">Cadastrar</Button>
        </form>
      </Container>
    </>
  );
} 