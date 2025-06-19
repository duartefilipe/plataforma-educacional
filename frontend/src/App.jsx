import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Login from './pages/Login';
import AdminDashboard from './pages/AdminDashboard';
import ProfessorDashboard from './pages/ProfessorDashboard';
import AlunoDashboard from './pages/AlunoDashboard';
import NotFound from './pages/NotFound';
import CadastrarProfessor from './pages/CadastrarProfessor';
import CadastrarAluno from './pages/CadastrarAluno';
import PrivateRoute from './components/PrivateRoute';

function App() {
  return (
    <Routes>
      <Route path="/" element={<Login />} />
      <Route path="/admin" element={
        <PrivateRoute role="ADMIN">
          <AdminDashboard />
        </PrivateRoute>
      } />
      <Route path="/professor" element={
        <PrivateRoute role="PROFESSOR">
          <ProfessorDashboard />
        </PrivateRoute>
      } />
      <Route path="/aluno" element={
        <PrivateRoute role="ALUNO">
          <AlunoDashboard />
        </PrivateRoute>
      } />
      <Route path="/cadastrar-professor" element={<CadastrarProfessor />} />
      <Route path="/cadastrar-aluno" element={<CadastrarAluno />} />
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}

export default App;
