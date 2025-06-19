import React from 'react';
import { AppBar, Toolbar, Typography, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const handleHomeClick = () => {
    if (user?.role === 'ADMIN') navigate('/admin');
    else if (user?.role === 'PROFESSOR') navigate('/professor');
    else if (user?.role === 'ALUNO') navigate('/aluno');
    else navigate('/');
  };

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography 
          variant="h6" 
          style={{ flexGrow: 1, cursor: 'pointer' }} 
          onClick={handleHomeClick}
        >
          Plataforma Educacional
        </Typography>
        {user?.role === 'ADMIN' && (
          <>
            <Button color="inherit" onClick={() => navigate('/cadastrar-professor')}>
              Cadastrar Professor
            </Button>
            <Button color="inherit" onClick={() => navigate('/cadastrar-aluno')}>
              Cadastrar Aluno
            </Button>
          </>
        )}
        {user?.email && (
          <Typography variant="body1" style={{ marginRight: 16 }}>
            {user.email}
          </Typography>
        )}
        {user?.email && (
          <Button color="inherit" onClick={handleLogout}>
            Logout
          </Button>
        )}
      </Toolbar>
    </AppBar>
  );
} 