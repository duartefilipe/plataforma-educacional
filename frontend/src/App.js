import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import Login from './pages/Login';
import AdminDashboard from './pages/AdminDashboard';
import ProtectedLayout from './components/ProtectedLayout';
import CadastrarUsuario from './pages/CadastrarUsuario';
import CadastrarEscola from './pages/CadastrarEscola';
import CadastrarTurma from './pages/CadastrarTurma';
import ListarUsuarios from './pages/ListarUsuarios';
import ListarEscolas from './pages/ListarEscolas';
import ListarTurmas from './pages/ListarTurmas';
import EditarUsuario from './pages/EditarUsuario';
import EditarEscola from './pages/EditarEscola';
import CadastrarAtividade from './pages/CadastrarAtividade';
import ListarAtividades from './pages/ListarAtividades';
import EditarAtividade from './pages/EditarAtividade';
import DesignarAtividade from './pages/DesignarAtividade';
import ProfessorDashboard from './pages/ProfessorDashboard';
import { AuthProvider } from './context/AuthContext';
import { Container } from '@mui/material';
import Navbar from './components/Navbar';
import EditarTurma from './pages/EditarTurma';

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
            <Route path="/login" element={<Login />} />
            <Route path="/" element={<ProtectedLayout><AdminDashboard /></ProtectedLayout>} />
            
            {/* Rotas do Admin */}
            <Route path="/cadastrar-usuario" element={<ProtectedLayout><CadastrarUsuario /></ProtectedLayout>} />
            <Route path="/cadastrar-escola" element={<ProtectedLayout><CadastrarEscola /></ProtectedLayout>} />
            <Route path="/cadastrar-turma" element={<ProtectedLayout><CadastrarTurma /></ProtectedLayout>} />
            <Route path="/listar-usuarios" element={<ProtectedLayout><ListarUsuarios /></ProtectedLayout>} />
            <Route path="/listar-escolas" element={<ProtectedLayout><ListarEscolas /></ProtectedLayout>} />
            <Route path="/listar-turmas" element={<ProtectedLayout><ListarTurmas /></ProtectedLayout>} />
            <Route path="/editar-usuario/:id" element={<ProtectedLayout><EditarUsuario /></ProtectedLayout>} />
            <Route path="/editar-escola/:id" element={<ProtectedLayout><EditarEscola /></ProtectedLayout>} />
            <Route path="/editar-turma/:id" element={<ProtectedLayout><EditarTurma /></ProtectedLayout>} />
            
            {/* Rotas do Professor */}
            <Route path="/professor" element={<ProtectedLayout><ProfessorDashboard /></ProtectedLayout>} />
            <Route path="/professor/cadastrar-atividade" element={<ProtectedLayout><CadastrarAtividade /></ProtectedLayout>} />
            <Route path="/professor/listar-atividades" element={<ProtectedLayout><ListarAtividades /></ProtectedLayout>} />
            <Route path="/professor/editar-atividade/:id" element={<ProtectedLayout><EditarAtividade /></ProtectedLayout>} />
            <Route path="/professor/designar-atividade/:id" element={<ProtectedLayout><DesignarAtividade /></ProtectedLayout>} />
          </Routes>
        </Container>
      </div>
    </AuthProvider>
  );
}

export default App;
