import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Login from './pages/Login';
import AdminDashboard from './pages/AdminDashboard';
import ProfessorDashboard from './pages/ProfessorDashboard';
import AlunoDashboard from './pages/AlunoDashboard';
import NotFound from './pages/NotFound';

function App() {
  return (
    <Routes>
      <Route path="/" element={<Login />} />
      <Route path="/admin" element={<AdminDashboard />} />
      <Route path="/professor" element={<ProfessorDashboard />} />
      <Route path="/aluno" element={<AlunoDashboard />} />
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}

export default App;
