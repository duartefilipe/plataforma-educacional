import React from 'react';
import { Typography, Container, Paper } from '@mui/material';

function AdminDashboard() {
  return (
    <Container maxWidth="lg" style={{ marginTop: '2rem' }}>
      <Paper elevation={3} style={{ padding: '2rem' }}>
        <Typography variant="h4" gutterBottom>
          Bem-vindo ao Painel do Administrador
        </Typography>
        <Typography variant="body1">
          Utilize a barra de navegação acima para gerenciar os cadastros e listagens do sistema.
        </Typography>
      </Paper>
    </Container>
  );
}

export default AdminDashboard; 