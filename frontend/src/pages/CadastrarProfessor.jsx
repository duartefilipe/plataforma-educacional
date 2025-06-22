import React from 'react';
import UserForm from '../components/UserForm';
import api from '../api/axiosConfig';
import { useNavigate } from 'react-router-dom';

const CadastrarProfessor = () => {
  const navigate = useNavigate();

  const handleSubmit = async (userData) => {
    try {
      await api.post('/users', userData);
      alert('Professor criado com sucesso!');
      navigate('/admin');
    } catch (err) {
      alert(`Erro ao criar professor: ${err.response?.data || 'Tente novamente.'}`);
    }
  };

  return (
    <UserForm
      role="PROFESSOR"
      title="Cadastrar Novo Professor"
      onSubmit={handleSubmit}
    />
  );
};

export default CadastrarProfessor; 