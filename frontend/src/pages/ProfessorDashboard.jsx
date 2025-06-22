import React from 'react';
import { Typography, Container, Paper } from '@mui/material';

function ProfessorDashboard() {
  return (
    <Container maxWidth="lg" style={{ marginTop: '2rem' }}>
      <Paper elevation={3} style={{ padding: '2rem' }}>
        <Typography variant="h4" gutterBottom>
          Bem-vindo ao Painel do Professor
        </Typography>
        <Typography variant="body1">
          Utilize a barra de navegação acima para gerenciar suas atividades.
        </Typography>
      </Paper>
    </Container>
  );
}

export default ProfessorDashboard; 