import React, { useState } from 'react';
import { AppBar, Toolbar, Typography, Button, Menu, MenuItem } from '@mui/material';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';

const Navbar = () => {
    const { user, logout } = useAuth();

    const [anchorElUsuarios, setAnchorElUsuarios] = useState(null);
    const [anchorElEscolas, setAnchorElEscolas] = useState(null);
    const [anchorElTurmas, setAnchorElTurmas] = useState(null);

    const handleMenuOpen = (event, setAnchorEl) => {
        setAnchorEl(event.currentTarget);
    };

    const handleMenuClose = (setAnchorEl) => {
        setAnchorEl(null);
    };

    const handleHomeClick = () => {
        const path = user?.role === 'ADMIN' ? '/' : '/professor';
        // Simple navigation, no need for navigate hook here if Link is not used.
        // But for consistency let's just use window location change for this simple case or use Link on Typography
        window.location.href = path;
    };

    return (
        <AppBar position="static">
            <Toolbar>
                <Typography variant="h6" component={Link} to={user?.role === 'ADMIN' ? '/' : '/professor'} sx={{ flexGrow: 1, cursor: 'pointer', color: 'inherit', textDecoration: 'none' }}>
                    Plataforma Educacional
                </Typography>
                {user && (
                    <>
                        {user.role === 'ADMIN' && (
                            <>
                                {/* Menu Usuários */}
                                <Button
                                    color="inherit"
                                    onClick={(e) => handleMenuOpen(e, setAnchorElUsuarios)}
                                    endIcon={<ArrowDropDownIcon />}
                                >
                                    Usuários
                                </Button>
                                <Menu
                                    anchorEl={anchorElUsuarios}
                                    open={Boolean(anchorElUsuarios)}
                                    onClose={() => handleMenuClose(setAnchorElUsuarios)}
                                >
                                    <MenuItem component={Link} to="/cadastrar-usuario" onClick={() => handleMenuClose(setAnchorElUsuarios)}>Cadastrar</MenuItem>
                                    <MenuItem component={Link} to="/listar-usuarios" onClick={() => handleMenuClose(setAnchorElUsuarios)}>Listar</MenuItem>
                                </Menu>

                                {/* Menu Escolas */}
                                <Button
                                    color="inherit"
                                    onClick={(e) => handleMenuOpen(e, setAnchorElEscolas)}
                                    endIcon={<ArrowDropDownIcon />}
                                >
                                    Escolas
                                </Button>
                                <Menu
                                    anchorEl={anchorElEscolas}
                                    open={Boolean(anchorElEscolas)}
                                    onClose={() => handleMenuClose(setAnchorElEscolas)}
                                >
                                    <MenuItem component={Link} to="/cadastrar-escola" onClick={() => handleMenuClose(setAnchorElEscolas)}>Cadastrar</MenuItem>
                                    <MenuItem component={Link} to="/listar-escolas" onClick={() => handleMenuClose(setAnchorElEscolas)}>Listar</MenuItem>
                                </Menu>

                                {/* Menu Turmas */}
                                <Button
                                    color="inherit"
                                    onClick={(e) => handleMenuOpen(e, setAnchorElTurmas)}
                                    endIcon={<ArrowDropDownIcon />}
                                >
                                    Turmas
                                </Button>
                                <Menu
                                    anchorEl={anchorElTurmas}
                                    open={Boolean(anchorElTurmas)}
                                    onClose={() => handleMenuClose(setAnchorElTurmas)}
                                >
                                    <MenuItem component={Link} to="/cadastrar-turma" onClick={() => handleMenuClose(setAnchorElTurmas)}>Cadastrar</MenuItem>
                                    <MenuItem component={Link} to="/listar-turmas" onClick={() => handleMenuClose(setAnchorElTurmas)}>Listar</MenuItem>
                                </Menu>
                            </>
                        )}
                        {user.role === 'PROFESSOR' && (
                            <>
                                <Button color="inherit" component={Link} to="/professor/cadastrar-atividade">
                                    Cadastrar Atividade
                                </Button>
                                <Button color="inherit" component={Link} to="/professor/listar-atividades">
                                    Listar Atividades
                                </Button>
                            </>
                        )}
                        <Typography sx={{ mx: 2 }}>{user.email}</Typography>
                        <Button color="inherit" onClick={logout}>
                            Logout
                        </Button>
                    </>
                )}
            </Toolbar>
        </AppBar>
    );
};

export default Navbar; 