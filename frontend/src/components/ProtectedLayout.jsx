import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedLayout({ role }) {
  const { user } = useAuth();

  if (!user) {
    // Se não estiver logado, redireciona para a página de login
    return <Navigate to="/" />;
  }

  if (role && user.role !== role) {
    // Se estiver logado, mas não tiver a role correta, redireciona para o login
    // Em uma aplicação real, poderia redirecionar para uma página de "não autorizado"
    return <Navigate to="/" />;
  }

  // Se estiver logado e tiver a role correta, renderiza o conteúdo da rota
  return (
    <>
      <Outlet />
    </>
  );
} 