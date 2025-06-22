import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import Login from './pages/Login';
import AdminDashboard from './pages/AdminDashboard';
import ProtectedLayout from './components/ProtectedLayout';
import CadastrarProfessor from './pages/CadastrarProfessor';
import CadastrarAluno from './pages/CadastrarAluno';
import CadastrarAdmin from './pages/CadastrarAdmin';
import ListarUsuarios from './pages/ListarUsuarios';
import EditarUsuario from './pages/EditarUsuario';
import ProfessorDashboard from './pages/ProfessorDashboard';
import CadastrarAtividade from './pages/CadastrarAtividade';
import ListarAtividades from './pages/ListarAtividades';
import CadastrarEscola from './pages/CadastrarEscola';
import ListarEscolas from './pages/ListarEscolas';
import { AuthProvider } from './context/AuthContext';
import { Container } from '@mui/material';
import Navbar from './components/Navbar';
import EditarAtividade from './pages/EditarAtividade';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

function App() {
  return (
    <AuthProvider>
      <div className="App">
        <Navbar />
        <Container>
          <Routes>
            {/* Rotas Públicas */}
            <Route path="/" element={<Login />} />

            {/* Rotas Protegidas */}
            <Route element={<ProtectedLayout />}>
              {/* Dashboards */}
              <Route path="/admin" element={<AdminDashboard />} />
              <Route path="/professor" element={<ProfessorDashboard />} />

              {/* Rotas de Admin */}
              <Route path="/admin/cadastrar-usuario" element={<CadastrarAdmin />} />
              <Route path="/admin/cadastrar-professor" element={<CadastrarProfessor />} />
              <Route path="/admin/cadastrar-aluno" element={<CadastrarAluno />} />
              <Route path="/admin/listar-usuarios" element={<ListarUsuarios />} />
              <Route path="/admin/editar-usuario/:id" element={<EditarUsuario />} />
              <Route path="/admin/cadastrar-escola" element={<CadastrarEscola />} />
              <Route path="/admin/listar-escolas" element={<ListarEscolas />} />

              {/* Rotas de Professor */}
              <Route path="/professor/cadastrar-atividade" element={<CadastrarAtividade />} />
              <Route path="/professor/atividades" element={<ListarAtividades />} />
              <Route path="/professor/editar-atividade/:id" element={<EditarAtividade />} />
            </Route>
          </Routes>
        </Container>
      </div>
    </AuthProvider>
  );
}

export default App;
