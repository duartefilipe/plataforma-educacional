import React, { useState, useEffect, useRef } from 'react';
import {
  Box,
  Container,
  Typography,
  TextField,
  Button,
  Paper,
  AppBar,
  Toolbar,
  IconButton,
  Tooltip,
  Chip,
  Alert,
  Snackbar,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Divider,
  Grid,
  Card,
  CardContent,
  LinearProgress,
  Fab,
  Zoom,
  useTheme,
  useMediaQuery
} from '@mui/material';
import {
  Save,
  CloudUpload,
  FormatBold,
  FormatItalic,
  FormatUnderlined,
  FormatListBulleted,
  FormatListNumbered,
  FormatAlignLeft,
  FormatAlignCenter,
  FormatAlignRight,
  InsertLink,
  Image,
  AttachFile,
  Preview,
  Close,
  Check,
  Edit,
  Download,
  Share,
  MoreVert,
  Undo,
  Redo,
  Title,
  Description,
  School,
  Assignment
} from '@mui/icons-material';
import api from '../api/axiosConfig';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';

const CadastrarAtividade = () => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const { user } = useAuth();
  const navigate = useNavigate();
  const editorRef = useRef(null);
  const fileInputRef = useRef(null);

  // Estados principais
  const [titulo, setTitulo] = useState('');
  const [descricao, setDescricao] = useState('');
  const [tipoConteudo, setTipoConteudo] = useState('TEXTO');
  const [arquivo, setArquivo] = useState(null);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [previewMode, setPreviewMode] = useState(false);
  const [showSaveDialog, setShowSaveDialog] = useState(false);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

  // Estados do editor
  const [editorContent, setEditorContent] = useState('');
  const [selectedText, setSelectedText] = useState('');
  const [canUndo, setCanUndo] = useState(false);
  const [canRedo, setCanRedo] = useState(false);

  // Estados de upload
  const [uploadProgress, setUploadProgress] = useState(0);
  const [uploading, setUploading] = useState(false);

  // Adicionar estado para tipo de criação
  const [tipoCriacao, setTipoCriacao] = useState('NOVA'); // 'NOVA' ou 'COMPARTILHAR'
  const [atividadesExistentes, setAtividadesExistentes] = useState([]);
  const [atividadeCompartilharId, setAtividadeCompartilharId] = useState('');

  // Adicionar novos estados para o campo de seleção de professor e escola
  const [professorId, setProfessorId] = useState('');
  const [escolaId, setEscolaId] = useState('');
  const [professores, setProfessores] = useState([]);
  const [escolas, setEscolas] = useState([]);

  // Buscar atividades existentes se for compartilhar
  useEffect(() => {
    if (tipoCriacao === 'COMPARTILHAR') {
      api.get('/atividades/professor/me').then(res => {
        setAtividadesExistentes(res.data);
      });
    }
  }, [tipoCriacao]);

  // Buscar professores e escolas se for ADMIN
  useEffect(() => {
    if (user?.role === 'ADMIN') {
      api.get('/professores').then(res => {
        setProfessores(res.data);
      });
      api.get('/escolas').then(res => {
        setEscolas(res.data);
      });
    }
  }, [user?.role]);

  // Autosave
  useEffect(() => {
    const autosaveTimer = setTimeout(() => {
      if (titulo || descricao || editorContent) {
        handleAutosave();
      }
    }, 30000); // Autosave a cada 30 segundos

    return () => clearTimeout(autosaveTimer);
  }, [titulo, descricao, editorContent]);

  const handleAutosave = async () => {
    if (!titulo || saving) return;
    
    try {
      setSaving(true);
      const formData = new FormData();
      const atividadeDTO = { 
        titulo, 
        tipoConteudo, 
        conteudoTexto: editorContent,
        ...(user?.role === 'ADMIN' && { professorId, escolaId })
      };
      formData.append('atividade', new Blob([JSON.stringify(atividadeDTO)], { type: 'application/json' }));
      
      if (arquivo && tipoConteudo === 'ARQUIVO_UPLOAD') {
        formData.append('arquivo', arquivo);
      }

      await api.post('/atividades', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
        onUploadProgress: (progressEvent) => {
          const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          setUploadProgress(progress);
        }
      });
      
      showSnackbar('Rascunho salvo automaticamente', 'success');
    } catch (err) {
      console.error('Erro no autosave:', err);
    } finally {
      setSaving(false);
      setUploadProgress(0);
    }
  };

  const showSnackbar = (message, severity = 'success') => {
    setSnackbar({ open: true, message, severity });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!titulo.trim() && tipoCriacao === 'NOVA') {
      showSnackbar('Título é obrigatório', 'error');
      return;
    }
    if (tipoCriacao === 'COMPARTILHAR' && !atividadeCompartilharId) {
      showSnackbar('Selecione uma atividade para compartilhar', 'error');
      return;
    }
    setLoading(true);
    try {
      if (tipoCriacao === 'COMPARTILHAR') {
        // Compartilhar atividade existente
        await api.post(`/atividades/${atividadeCompartilharId}/compartilhar`, {});
        showSnackbar('Atividade compartilhada com sucesso!', 'success');
        setTimeout(() => navigate('/professor/atividades'), 1500);
        return;
      }
      const formData = new FormData();
      const atividadeDTO = { 
        titulo, 
        tipoConteudo, 
        conteudoTexto: editorContent,
        ...(user?.role === 'ADMIN' && { professorId, escolaId })
      };
      formData.append('atividade', new Blob([JSON.stringify(atividadeDTO)], { type: 'application/json' }));
      
      if (arquivo && tipoConteudo === 'ARQUIVO_UPLOAD') {
        formData.append('arquivo', arquivo);
      }

      await api.post('/atividades', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
        onUploadProgress: (progressEvent) => {
          const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          setUploadProgress(progress);
        }
      });

      showSnackbar('Atividade criada com sucesso!', 'success');
      setTimeout(() => navigate('/professor/atividades'), 1500);
    } catch (err) {
      console.error("Falha ao criar atividade:", err);
      showSnackbar(`Erro ao criar atividade: ${err.response?.data?.message || 'Tente novamente.'}`, 'error');
    } finally {
      setLoading(false);
      setUploadProgress(0);
    }
  };

  const handleFileUpload = (event) => {
    const file = event.target.files[0];
    if (file) {
      setArquivo(file);
      showSnackbar(`Arquivo "${file.name}" anexado`, 'success');
    }
  };

  const handleEditorFormat = (command) => {
    if (editorRef.current) {
      document.execCommand(command, false, null);
      editorRef.current.focus();
    }
  };

  const handleEditorChange = (e) => {
    setEditorContent(e.target.innerHTML);
  };

  const handleKeyDown = (e) => {
    if (e.ctrlKey || e.metaKey) {
      switch (e.key) {
        case 's':
          e.preventDefault();
          handleSubmit(e);
          break;
        case 'z':
          e.preventDefault();
          document.execCommand('undo', false, null);
          break;
        case 'y':
          e.preventDefault();
          document.execCommand('redo', false, null);
          break;
      }
    }
  };

  const ToolbarButton = ({ icon, tooltip, onClick, disabled = false, active = false }) => (
    <Tooltip title={tooltip}>
      <IconButton 
        onClick={onClick} 
        disabled={disabled}
        sx={{ 
          color: active ? 'primary.main' : 'inherit',
          '&:hover': { backgroundColor: 'action.hover' }
        }}
      >
        {icon}
      </IconButton>
    </Tooltip>
  );

  const renderToolbar = () => (
    <AppBar position="sticky" color="default" elevation={1}>
      <Toolbar variant="dense">
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, flexGrow: 1 }}>
          <ToolbarButton 
            icon={<FormatBold />} 
            tooltip="Negrito (Ctrl+B)" 
            onClick={() => handleEditorFormat('bold')} 
          />
          <ToolbarButton 
            icon={<FormatItalic />} 
            tooltip="Itálico (Ctrl+I)" 
            onClick={() => handleEditorFormat('italic')} 
          />
          <ToolbarButton 
            icon={<FormatUnderlined />} 
            tooltip="Sublinhado (Ctrl+U)" 
            onClick={() => handleEditorFormat('underline')} 
          />
          
          <Divider orientation="vertical" flexItem sx={{ mx: 1 }} />
          
          <ToolbarButton 
            icon={<FormatListBulleted />} 
            tooltip="Lista com marcadores" 
            onClick={() => handleEditorFormat('insertUnorderedList')} 
          />
          <ToolbarButton 
            icon={<FormatListNumbered />} 
            tooltip="Lista numerada" 
            onClick={() => handleEditorFormat('insertOrderedList')} 
          />
          
          <Divider orientation="vertical" flexItem sx={{ mx: 1 }} />
          
          <ToolbarButton 
            icon={<FormatAlignLeft />} 
            tooltip="Alinhar à esquerda" 
            onClick={() => handleEditorFormat('justifyLeft')} 
          />
          <ToolbarButton 
            icon={<FormatAlignCenter />} 
            tooltip="Centralizar" 
            onClick={() => handleEditorFormat('justifyCenter')} 
          />
          <ToolbarButton 
            icon={<FormatAlignRight />} 
            tooltip="Alinhar à direita" 
            onClick={() => handleEditorFormat('justifyRight')} 
          />
          
          <Divider orientation="vertical" flexItem sx={{ mx: 1 }} />
          
          <ToolbarButton 
            icon={<InsertLink />} 
            tooltip="Inserir link" 
            onClick={() => {
              const url = prompt('Digite a URL:');
              if (url) document.execCommand('createLink', false, url);
            }} 
          />
          <ToolbarButton 
            icon={<Image />} 
            tooltip="Inserir imagem" 
            onClick={() => fileInputRef.current?.click()} 
          />
        </Box>
        
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Tooltip title="Visualizar">
            <IconButton onClick={() => setPreviewMode(!previewMode)}>
              <Preview />
            </IconButton>
          </Tooltip>
          <Tooltip title="Salvar (Ctrl+S)">
            <IconButton onClick={handleSubmit} disabled={loading}>
              <Save />
            </IconButton>
          </Tooltip>
        </Box>
      </Toolbar>
    </AppBar>
  );

  const renderEditor = () => (
    <Box sx={{ height: '60vh', border: '1px solid', borderColor: 'divider', borderRadius: 1 }}>
      <ReactQuill
        theme="snow"
        value={editorContent}
        onChange={setEditorContent}
        style={{ height: '55vh', background: '#fff' }}
        placeholder="Digite o texto da atividade..."
      />
    </Box>
  );

  const renderPreview = () => (
    <Paper sx={{ p: 3, height: '60vh', overflowY: 'auto' }}>
      <Typography variant="h4" gutterBottom>{titulo || 'Sem título'}</Typography>
      <Divider sx={{ mb: 2 }} />
      <div dangerouslySetInnerHTML={{ __html: descricao || editorContent }} />
      {arquivo && (
        <Box sx={{ mt: 2, p: 2, bgcolor: 'grey.50', borderRadius: 1 }}>
          <Typography variant="subtitle2" gutterBottom>Anexo:</Typography>
          <Chip 
            icon={<AttachFile />} 
            label={arquivo.name} 
            variant="outlined"
            onDelete={() => setArquivo(null)}
          />
        </Box>
      )}
    </Paper>
  );

  return (
    <Box sx={{ minHeight: '100vh', bgcolor: 'grey.50' }}>
      {/* Header */}
      <AppBar position="static" color="primary" elevation={0}>
        <Toolbar>
          <Assignment sx={{ mr: 2 }} />
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            {titulo ? `Editando: ${titulo}` : 'Nova Atividade'}
          </Typography>
          <Box sx={{ display: 'flex', gap: 1 }}>
            <Button 
              color="inherit" 
              startIcon={<Close />}
              onClick={() => navigate('/professor/atividades')}
            >
              Cancelar
            </Button>
            <Button 
              variant="contained" 
              color="secondary"
              startIcon={<Save />}
              onClick={handleSubmit}
              disabled={loading || !titulo.trim()}
            >
              {loading ? 'Salvando...' : 'Salvar Atividade'}
            </Button>
          </Box>
        </Toolbar>
      </AppBar>

      {/* Progress Bar */}
      {(loading || uploading) && (
        <LinearProgress 
          variant="determinate" 
          value={uploadProgress} 
          sx={{ position: 'sticky', top: 0, zIndex: 1000 }}
        />
      )}

      <Container maxWidth="xl" sx={{ mt: 3, mb: 3 }}>
        <Grid container spacing={3}>
          {/* Sidebar */}
          <Grid item xs={12} md={3}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  <School sx={{ mr: 1, verticalAlign: 'middle' }} />
                  Configurações
                </Typography>
                
                <FormControl fullWidth margin="normal" size="small">
                  <InputLabel>Tipo de Criação</InputLabel>
                  <Select
                    value={tipoCriacao}
                    label="Tipo de Criação"
                    onChange={e => setTipoCriacao(e.target.value)}
                  >
                    <MenuItem value="NOVA">Nova Atividade</MenuItem>
                    <MenuItem value="COMPARTILHAR">Compartilhar Atividade</MenuItem>
                  </Select>
                </FormControl>

                <TextField
                  label="Título da Atividade"
                  fullWidth
                  margin="normal"
                  value={titulo}
                  onChange={(e) => setTitulo(e.target.value)}
                  placeholder="Digite o título..."
                  variant="outlined"
                  size="small"
                />

                <FormControl fullWidth margin="normal" size="small">
                  <InputLabel>Tipo de Conteúdo</InputLabel>
                  <Select
                    value={tipoConteudo}
                    label="Tipo de Conteúdo"
                    onChange={(e) => setTipoConteudo(e.target.value)}
                  >
                    <MenuItem value="TEXTO">Texto Rico</MenuItem>
                    <MenuItem value="ARQUIVO_UPLOAD">Arquivo</MenuItem>
                  </Select>
                </FormControl>

                {tipoConteudo === 'ARQUIVO_UPLOAD' && (
                  <Box sx={{ mt: 2 }}>
                    <Button
                      variant="outlined"
                      component="label"
                      fullWidth
                      startIcon={<CloudUpload />}
                      size="small"
                    >
                      Anexar Arquivo
                      <input
                        ref={fileInputRef}
                        type="file"
                        hidden
                        onChange={handleFileUpload}
                        accept=".pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx,.txt,.jpg,.jpeg,.png,.gif"
                      />
                    </Button>
                    {arquivo && (
                      <Chip 
                        icon={<AttachFile />} 
                        label={arquivo.name} 
                        onDelete={() => setArquivo(null)}
                        sx={{ mt: 1, width: '100%' }}
                      />
                    )}
                  </Box>
                )}

                {/* Campo de seleção de professor - apenas para ADMIN */}
                {user?.role === 'ADMIN' && (
                  <FormControl fullWidth margin="normal" size="small">
                    <InputLabel>Professor</InputLabel>
                    <Select
                      value={professorId}
                      label="Professor"
                      onChange={(e) => setProfessorId(e.target.value)}
                    >
                      {professores?.map(prof => (
                        <MenuItem key={prof.id} value={prof.id}>
                          {prof.nomeCompleto || prof.nome}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                )}

                {/* Campo de seleção de escola - apenas para ADMIN */}
                {user?.role === 'ADMIN' && (
                  <FormControl fullWidth margin="normal" size="small">
                    <InputLabel>Escola</InputLabel>
                    <Select
                      value={escolaId}
                      label="Escola"
                      onChange={(e) => setEscolaId(e.target.value)}
                    >
                      {escolas?.map(esc => (
                        <MenuItem key={esc.id} value={esc.id}>
                          {esc.nome}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                )}

                <Divider sx={{ my: 2 }} />
                
                <Typography variant="body2" color="text.secondary">
                  <strong>Dicas:</strong>
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                  • Use Ctrl+S para salvar rapidamente
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  • Ctrl+Z para desfazer
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  • Use a barra de ferramentas para formatação
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          {/* Main Content */}
          <Grid item xs={12} md={9}>
            <Card>
              <CardContent sx={{ p: 0 }}>
                {renderToolbar()}
                <Box sx={{ p: 3 }}>
                  {previewMode ? renderPreview() : renderEditor()}
                </Box>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </Container>

      {/* Floating Action Button */}
      <Zoom in={!isMobile}>
        <Fab
          color="primary"
          sx={{ position: 'fixed', bottom: 16, right: 16 }}
          onClick={handleSubmit}
          disabled={loading || !titulo.trim()}
        >
          <Save />
        </Fab>
      </Zoom>

      {/* Snackbar */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
      >
        <Alert 
          onClose={() => setSnackbar({ ...snackbar, open: false })} 
          severity={snackbar.severity}
          sx={{ width: '100%' }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>

      {/* Save Dialog */}
      <Dialog open={showSaveDialog} onClose={() => setShowSaveDialog(false)}>
        <DialogTitle>Salvar Atividade</DialogTitle>
        <DialogContent>
          <Typography>
            Deseja salvar as alterações antes de sair?
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setShowSaveDialog(false)}>Cancelar</Button>
          <Button onClick={() => {
            handleSubmit();
            setShowSaveDialog(false);
          }} variant="contained">
            Salvar
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default CadastrarAtividade; 