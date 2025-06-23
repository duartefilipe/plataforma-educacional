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
import { AuthProvider, useAuth } from './context/AuthContext';
import { Container } from '@mui/material';
import Navbar from './components/Navbar';
import EditarTurma from './pages/EditarTurma';
import ListarAtividadesCompartilhadas from './pages/ListarAtividadesCompartilhadas';
import AtividadesFavoritas from './pages/AtividadesFavoritas';
import AdminAtividadesCompartilhadas from './pages/AdminAtividadesCompartilhadas';
import CadastrarAtividadeCompartilhada from './pages/CadastrarAtividadeCompartilhada';
import ProfessorEscolas from './pages/ProfessorEscolas';
import ProfessorTurmas from './pages/ProfessorTurmas';
import ProfessorAtividadesTurma from './pages/ProfessorAtividadesTurma';
import VincularProfessorEscola from './pages/VincularProfessorEscola';
import ProfessorAtividades from './pages/ProfessorAtividades';
import ProfessorTarefas from './pages/ProfessorTarefas';

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
  const { user } = useAuth();

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
            <Route path="/editar-atividade/:id" element={<ProtectedLayout allowedRoles={['ADMIN']}><EditarAtividade /></ProtectedLayout>} />
            
            {/* Rotas do Professor */}
            <Route path="/professor" element={<ProtectedLayout><ProfessorDashboard /></ProtectedLayout>} />
            <Route path="/professor/cadastrar-atividade" element={<ProtectedLayout><CadastrarAtividade /></ProtectedLayout>} />
            <Route path="/professor/listar-atividades" element={<ProtectedLayout><ListarAtividades /></ProtectedLayout>} />
            <Route path="/professor/editar-atividade/:id" element={<ProtectedLayout><EditarAtividade /></ProtectedLayout>} />
            <Route path="/professor/designar-atividade/:id" element={<ProtectedLayout><DesignarAtividade /></ProtectedLayout>} />
            <Route path="/professor/atividades" element={<ProtectedLayout><ProfessorAtividades /></ProtectedLayout>} />
            <Route path="/professor/atividades-compartilhadas" element={<ProtectedLayout><ListarAtividadesCompartilhadas /></ProtectedLayout>} />
            <Route path="/professor/atividades-favoritas" element={<ProtectedLayout><AtividadesFavoritas /></ProtectedLayout>} />
            <Route path="/professor/tarefas" element={<ProtectedLayout><ProfessorTarefas /></ProtectedLayout>} />
            {user?.role === 'ADMIN' && (
              <Route path="/admin/atividades-compartilhadas" element={<ProtectedLayout><AdminAtividadesCompartilhadas /></ProtectedLayout>} />
            )}
            <Route path="/cadastrar-atividade-compartilhada" element={<ProtectedLayout><CadastrarAtividadeCompartilhada /></ProtectedLayout>} />
            <Route path="/professor/escolas" element={<ProtectedLayout><ProfessorEscolas /></ProtectedLayout>} />
            <Route path="/professor/escola/:escolaId/turmas" element={<ProtectedLayout><ProfessorTurmas /></ProtectedLayout>} />
            <Route path="/professor/turma/:turmaId/atividades" element={<ProtectedLayout><ProfessorAtividadesTurma /></ProtectedLayout>} />
            <Route path="/admin/vincular-professor-escola" element={<ProtectedLayout allowedRoles={['ADMIN']}><VincularProfessorEscola /></ProtectedLayout>} />
          </Routes>
        </Container>
      </div>
    </AuthProvider>
  );
}

export default App;
