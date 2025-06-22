import React from 'react';
import UserForm from '../components/UserForm';
import api from '../api/axiosConfig';
import { useNavigate } from 'react-router-dom';

const CadastrarAdmin = () => {
  const navigate = useNavigate();

  const handleSubmit = async (userData) => {
    try {
      await api.post('/users', userData);
      alert('Administrador criado com sucesso!');
      navigate('/admin');
    } catch (err) {
      alert(`Erro ao criar administrador: ${err.response?.data?.message || 'Tente novamente.'}`);
    }
  };

  return (
    <UserForm
      role="ADMIN"
      title="Cadastrar Novo Administrador"
      onSubmit={handleSubmit}
    />
  );
};

export default CadastrarAdmin; 