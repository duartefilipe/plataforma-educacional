import React, { useState, useEffect } from 'react';
import {
    Container, Typography, Paper, TableContainer, Table, TableHead, TableBody, TableRow, TableCell,
    Button, IconButton, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle
} from '@mui/material';
import { Edit, Delete } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';

const ListarEscolas = () => {
    const navigate = useNavigate();
    const [escolas, setEscolas] = useState([]);
    const [openDialog, setOpenDialog] = useState(false);
    const [escolaToDelete, setEscolaToDelete] = useState(null);

    const fetchEscolas = async () => {
        try {
            const response = await api.get('/escolas');
            setEscolas(response.data);
        } catch (error) {
            console.error("Erro ao buscar escolas:", error);
            alert("Não foi possível carregar a lista de escolas.");
        }
    };

    useEffect(() => {
        fetchEscolas();
    }, []);

    const handleOpenDialog = (id) => {
        setEscolaToDelete(id);
        setOpenDialog(true);
    };

    const handleCloseDialog = () => {
        setEscolaToDelete(null);
        setOpenDialog(false);
    };

    const handleDelete = async () => {
        if (!escolaToDelete) return;
        try {
            await api.delete(`/escolas/${escolaToDelete}`);
            alert('Escola excluída com sucesso!');
            fetchEscolas(); // Re-fetch a lista
        } catch (err) {
            alert(`Erro ao excluir escola: ${err.response?.data?.message || 'Tente novamente.'}`);
        } finally {
            handleCloseDialog();
        }
    };
    
    // TODO: Implementar a navegação para edição
    const handleEdit = (id) => {
        // navigate(`/admin/editar-escola/${id}`);
        alert("Funcionalidade de edição ainda não implementada.");
    };

    return (
        <Container maxWidth="lg" style={{ marginTop: '2rem' }}>
            <Typography variant="h4" gutterBottom>
                Lista de Escolas Cadastradas
            </Typography>
            <Paper elevation={3} style={{ padding: '1rem' }}>
                <TableContainer>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>ID</TableCell>
                                <TableCell>Nome</TableCell>
                                <TableCell>Email de Contato</TableCell>
                                <TableCell>Telefone</TableCell>
                                <TableCell>Ações</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {escolas.map((escola) => (
                                <TableRow key={escola.id}>
                                    <TableCell>{escola.id}</TableCell>
                                    <TableCell>{escola.nome}</TableCell>
                                    <TableCell>{escola.contatoEmail}</TableCell>
                                    <TableCell>{escola.contatoTelefone}</TableCell>
                                    <TableCell>
                                        <IconButton onClick={() => handleEdit(escola.id)} color="primary">
                                            <Edit />
                                        </IconButton>
                                        <IconButton onClick={() => handleOpenDialog(escola.id)} color="error">
                                            <Delete />
                                        </IconButton>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Paper>

            {/* Diálogo de Confirmação de Exclusão */}
            <Dialog
                open={openDialog}
                onClose={handleCloseDialog}
            >
                <DialogTitle>Confirmar Exclusão</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Você tem certeza que deseja excluir esta escola? Esta ação não pode ser desfeita.
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDialog}>Cancelar</Button>
                    <Button onClick={handleDelete} color="error" autoFocus>
                        Excluir
                    </Button>
                </DialogActions>
            </Dialog>
        </Container>
    );
};

export default ListarEscolas; 