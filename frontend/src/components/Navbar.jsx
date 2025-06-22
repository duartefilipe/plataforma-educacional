import React, { useState } from 'react';
import { AppBar, Toolbar, Typography, Button, Menu, MenuItem } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';

export default function Navbar() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const [anchorElCadastros, setAnchorElCadastros] = useState(null);
  const [anchorElListagens, setAnchorElListagens] = useState(null);

  const handleMenuOpen = (event, setter) => {
    setter(event.currentTarget);
  };

  const handleMenuClose = (setter) => {
    setter(null);
  };

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const handleNavigate = (path, setter) => {
    navigate(path);
    handleMenuClose(setter);
  };

  const handleHomeClick = () => {
    if (user?.role === 'ADMIN') {
      navigate('/admin');
    } else if (user?.role === 'PROFESSOR') {
      navigate('/professor');
    } else {
      navigate('/');
    }
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
            <Button
              color="inherit"
              onClick={(e) => handleMenuOpen(e, setAnchorElCadastros)}
              endIcon={<ArrowDropDownIcon />}
            >
              Cadastros
            </Button>
            <Menu
              anchorEl={anchorElCadastros}
              open={Boolean(anchorElCadastros)}
              onClose={() => handleMenuClose(setAnchorElCadastros)}
            >
              <MenuItem onClick={() => handleNavigate('/admin/cadastrar-usuario', setAnchorElCadastros)}>Admin</MenuItem>
              <MenuItem onClick={() => handleNavigate('/admin/cadastrar-professor', setAnchorElCadastros)}>Professor</MenuItem>
              <MenuItem onClick={() => handleNavigate('/admin/cadastrar-aluno', setAnchorElCadastros)}>Aluno</MenuItem>
              <MenuItem onClick={() => handleNavigate('/admin/cadastrar-escola', setAnchorElCadastros)}>Escola</MenuItem>
            </Menu>

            <Button
              color="inherit"
              onClick={(e) => handleMenuOpen(e, setAnchorElListagens)}
              endIcon={<ArrowDropDownIcon />}
            >
              Listagens
            </Button>
            <Menu
              anchorEl={anchorElListagens}
              open={Boolean(anchorElListagens)}
              onClose={() => handleMenuClose(setAnchorElListagens)}
            >
              <MenuItem onClick={() => handleNavigate('/admin/listar-usuarios', setAnchorElListagens)}>Usuários</MenuItem>
              <MenuItem onClick={() => handleNavigate('/admin/listar-escolas', setAnchorElListagens)}>Escolas</MenuItem>
            </Menu>
          </>
        )}

        {user?.role === 'PROFESSOR' && (
          <>
            <Button color="inherit" onClick={() => navigate('/professor/cadastrar-atividade')}>
              Cadastrar Atividade
            </Button>
            <Button color="inherit" onClick={() => navigate('/professor/atividades')}>
              Listar Atividades
            </Button>
          </>
        )}

        {user?.email && (
          <Typography variant="body1" style={{ marginLeft: 16, marginRight: 16 }}>
            {user.email}
          </Typography>
        )}
        {user && (
          <Button color="inherit" onClick={handleLogout}>
            Logout
          </Button>
        )}
      </Toolbar>
    </AppBar>
  );
} 