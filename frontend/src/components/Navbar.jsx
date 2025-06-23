import React, { useState } from 'react';
import { AppBar, Toolbar, Typography, Button, Menu, MenuItem } from '@mui/material';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';

const Navbar = () => {
    const { user, logout } = useAuth();

    // Menus separados
    const [anchorElUsuarios, setAnchorElUsuarios] = useState(null);
    const [anchorElProfessores, setAnchorElProfessores] = useState(null);
    const [anchorElEscolas, setAnchorElEscolas] = useState(null);
    const [anchorElAlunos, setAnchorElAlunos] = useState(null);
    const [anchorElTurmas, setAnchorElTurmas] = useState(null);
    const [anchorElAtividades, setAnchorElAtividades] = useState(null);
    const [anchorElAtividadesProf, setAnchorElAtividadesProf] = useState(null);

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
                {user && user.role === 'ADMIN' && (
                    <>
                        {/* Usuários (apenas admin) */}
                        <Button color="inherit" onClick={e => handleMenuOpen(e, setAnchorElUsuarios)} endIcon={<ArrowDropDownIcon />}>Usuários</Button>
                        <Menu anchorEl={anchorElUsuarios} open={Boolean(anchorElUsuarios)} onClose={() => handleMenuClose(setAnchorElUsuarios)}>
                            <MenuItem component={Link} to="/cadastrar-usuario?role=ADMIN" onClick={() => handleMenuClose(setAnchorElUsuarios)}>Cadastrar Usuário</MenuItem>
                            <MenuItem component={Link} to="/listar-usuarios" onClick={() => handleMenuClose(setAnchorElUsuarios)}>Listar Usuários</MenuItem>
                        </Menu>

                        {/* Professores */}
                        <Button color="inherit" onClick={e => handleMenuOpen(e, setAnchorElProfessores)} endIcon={<ArrowDropDownIcon />}>Professores</Button>
                        <Menu anchorEl={anchorElProfessores} open={Boolean(anchorElProfessores)} onClose={() => handleMenuClose(setAnchorElProfessores)}>
                            <MenuItem component={Link} to="/cadastrar-usuario?role=PROFESSOR" onClick={() => handleMenuClose(setAnchorElProfessores)}>Cadastrar Professor</MenuItem>
                            <MenuItem component={Link} to="/admin/vincular-professor-escola" onClick={() => handleMenuClose(setAnchorElProfessores)}>Vincular a Escola</MenuItem>
                            <MenuItem component={Link} to="/listar-usuarios?role=PROFESSOR" onClick={() => handleMenuClose(setAnchorElProfessores)}>Listar Professores</MenuItem>
                        </Menu>

                        {/* Escolas */}
                        <Button color="inherit" onClick={e => handleMenuOpen(e, setAnchorElEscolas)} endIcon={<ArrowDropDownIcon />}>Escolas</Button>
                        <Menu anchorEl={anchorElEscolas} open={Boolean(anchorElEscolas)} onClose={() => handleMenuClose(setAnchorElEscolas)}>
                            <MenuItem component={Link} to="/cadastrar-escola" onClick={() => handleMenuClose(setAnchorElEscolas)}>Cadastrar Escola</MenuItem>
                            <MenuItem component={Link} to="/listar-escolas" onClick={() => handleMenuClose(setAnchorElEscolas)}>Listar Escolas</MenuItem>
                        </Menu>

                        {/* Alunos */}
                        <Button color="inherit" onClick={e => handleMenuOpen(e, setAnchorElAlunos)} endIcon={<ArrowDropDownIcon />}>Alunos</Button>
                        <Menu anchorEl={anchorElAlunos} open={Boolean(anchorElAlunos)} onClose={() => handleMenuClose(setAnchorElAlunos)}>
                            <MenuItem component={Link} to="/cadastrar-usuario?role=ALUNO" onClick={() => handleMenuClose(setAnchorElAlunos)}>Cadastrar Aluno</MenuItem>
                            <MenuItem component={Link} to="/listar-usuarios?role=ALUNO" onClick={() => handleMenuClose(setAnchorElAlunos)}>Listar Alunos</MenuItem>
                        </Menu>

                        {/* Turmas */}
                        <Button color="inherit" onClick={e => handleMenuOpen(e, setAnchorElTurmas)} endIcon={<ArrowDropDownIcon />}>Turmas</Button>
                        <Menu anchorEl={anchorElTurmas} open={Boolean(anchorElTurmas)} onClose={() => handleMenuClose(setAnchorElTurmas)}>
                            <MenuItem component={Link} to="/cadastrar-turma" onClick={() => handleMenuClose(setAnchorElTurmas)}>Cadastrar Turma</MenuItem>
                            <MenuItem component={Link} to="/listar-turmas" onClick={() => handleMenuClose(setAnchorElTurmas)}>Listar Turmas</MenuItem>
                        </Menu>

                        {/* Atividades */}
                        <Button color="inherit" onClick={e => handleMenuOpen(e, setAnchorElAtividades)} endIcon={<ArrowDropDownIcon />}>Atividades</Button>
                        <Menu anchorEl={anchorElAtividades} open={Boolean(anchorElAtividades)} onClose={() => handleMenuClose(setAnchorElAtividades)}>
                            <MenuItem component={Link} to="/admin/atividades-compartilhadas" onClick={() => handleMenuClose(setAnchorElAtividades)}>Atividades Compartilhadas</MenuItem>
                        </Menu>
                    </>
                )}
                {user && user.role === 'PROFESSOR' && (
                    <>
                        <Button color="inherit" onClick={e => handleMenuOpen(e, setAnchorElAtividadesProf)} endIcon={<ArrowDropDownIcon />}>Atividades</Button>
                        <Menu anchorEl={anchorElAtividadesProf} open={Boolean(anchorElAtividadesProf)} onClose={() => handleMenuClose(setAnchorElAtividadesProf)}>
                            <MenuItem component={Link} to="/professor/cadastrar-atividade" onClick={() => handleMenuClose(setAnchorElAtividadesProf)}>Cadastrar Atividade</MenuItem>
                            <MenuItem component={Link} to="/cadastrar-atividade-compartilhada" onClick={() => handleMenuClose(setAnchorElAtividadesProf)}>Compartilhar Atividade</MenuItem>
                            <MenuItem component={Link} to="/professor/atividades" onClick={() => handleMenuClose(setAnchorElAtividadesProf)}>Minhas Atividades</MenuItem>
                            <MenuItem component={Link} to="/professor/atividades-compartilhadas" onClick={() => handleMenuClose(setAnchorElAtividadesProf)}>Compartilhadas</MenuItem>
                            <MenuItem component={Link} to="/professor/atividades-favoritas" onClick={() => handleMenuClose(setAnchorElAtividadesProf)}>Favoritas</MenuItem>
                            <MenuItem component={Link} to="/professor/tarefas" onClick={() => handleMenuClose(setAnchorElAtividadesProf)}>Tarefas</MenuItem>
                        </Menu>
                        <Button color="inherit" component={Link} to="/professor/escolas">
                            Minhas Escolas
                        </Button>
                    </>
                )}
                {user && (
                    <>
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