import React from 'react';
import { Button, Container, TextField, Typography } from '@mui/material';

function Login() {
  return (
    <Container maxWidth="sm" style={{ marginTop: '4rem' }}>
      <Typography variant="h4" gutterBottom>Login</Typography>
      <form>
        <TextField label="Email" fullWidth margin="normal" />
        <TextField label="Senha" type="password" fullWidth margin="normal" />
        <Button variant="contained" color="primary" fullWidth>Entrar</Button>
      </form>
    </Container>
  );
}

export default Login;
