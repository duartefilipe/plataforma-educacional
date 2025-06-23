import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Container, Typography, CircularProgress, Alert } from '@mui/material';
import api from '../api/axiosConfig';
import UserForm from '../components/UserForm'; // Reutilizando o formulário

const EditarUsuario = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [usuario, setUsuario] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchUsuario = async () => {
      try {
        setLoading(true);
        const response = await api.get(`/users/${id}`);
        setUsuario(response.data);
      } catch (err) {
        setError('Falha ao carregar dados do usuário.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchUsuario();
  }, [id]);

  const handleSubmit = async (userData) => {
    try {
      await api.put(`/users/${id}`, userData);
      alert('Usuário atualizado com sucesso!');
      navigate(-1); // Volta para a página anterior (a lista)
    } catch (err) {
      alert(`Erro ao atualizar usuário: ${err.response?.data?.message || 'Tente novamente.'}`);
    }
  };

  if (loading) return <CircularProgress />;
  if (error) return <Alert severity="error">{error}</Alert>;

  return (
    <Container maxWidth="sm" style={{ marginTop: '2rem' }}>
      <Typography variant="h4" gutterBottom>
        Editar Usuário
      </Typography>
      {usuario && (
        <UserForm
          initialData={usuario}
          onSubmit={handleSubmit}
          isEdit={true}
        />
      )}
    </Container>
  );
};

export default EditarUsuario; 