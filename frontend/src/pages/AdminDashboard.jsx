import React from 'react';
import { Button, Container, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';

export default function AdminDashboard() {
  const navigate = useNavigate();

  console.log('Renderizando AdminDashboard');

  const handleCadastrarProfessor = () => {
    console.log('Navegando para cadastro de professor');
    navigate('/cadastrar-professor');
  };

  const handleCadastrarAluno = () => {
    console.log('Navegando para cadastro de aluno');
    navigate('/cadastrar-aluno');
  };

  return (
    <div style={{ color: 'red', fontSize: 32 }}>PAINEL ADMIN TESTE</div>
  );
}
