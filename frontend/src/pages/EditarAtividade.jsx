import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
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
  useMediaQuery,
  CircularProgress,
  Switch,
  FormControlLabel
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
  Assignment,
  ArrowBack
} from '@mui/icons-material';
import axiosConfig from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';

const EditarAtividade = () => {
  const { id } = useParams();
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
  const [conteudoTexto, setConteudoTexto] = useState('');
  const [arquivo, setArquivo] = useState(null);
  const [nomeArquivoOriginal, setNomeArquivoOriginal] = useState('');
  const [professorId, setProfessorId] = useState('');
  const [escolaId, setEscolaId] = useState('');
  const [professores, setProfessores] = useState([]);
  const [escolas, setEscolas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [previewMode, setPreviewMode] = useState(false);
  const [showSaveDialog, setShowSaveDialog] = useState(false);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const [compartilhada, setCompartilhada] = useState(false);
  const [erroFavorito, setErroFavorito] = useState(false);
  const [erroRemoverCompartilhamento, setErroRemoverCompartilhamento] = useState('');

  // Estados do editor
  const [editorContent, setEditorContent] = useState('');
  const [selectedText, setSelectedText] = useState('');
  const [canUndo, setCanUndo] = useState(false);
  const [canRedo, setCanRedo] = useState(false);

  // Estados de upload
  const [uploadProgress, setUploadProgress] = useState(0);
  const [uploading, setUploading] = useState(false);

  useEffect(() => {
    const fetchAtividade = async () => {
      try {
        setLoading(true);
        const response = await axiosConfig.get(`/atividades/${id}`);
        const atividade = response.data;
        setTitulo(atividade.titulo);
        setDescricao(atividade.descricao);
        setTipoConteudo(atividade.tipoConteudo);
        setConteudoTexto(atividade.conteudoTexto || '');
        setEditorContent(atividade.conteudoTexto || atividade.descricao || '');
        setNomeArquivoOriginal(atividade.nomeArquivoOriginal || '');
        setProfessorId(atividade.professorCriadorId || '');
        setEscolaId(atividade.escolaId || '');
        // Verifica se está compartilhada
        try {
          await axiosConfig.get(`/atividades/compartilhadas/atividade/${id}`);
          setCompartilhada(true);
        } catch {
          setCompartilhada(false);
        }
      } catch (err) {
        showSnackbar('Erro ao carregar a atividade. ' + (err.response?.data?.message || err.message), 'error');
      } finally {
        setLoading(false);
      }
    };

    const fetchProfessoresEscolas = async () => {
      try {
        const profRes = await axiosConfig.get('/users/professores');
        setProfessores(profRes.data);
        const escRes = await axiosConfig.get('/escolas');
        setEscolas(escRes.data);
      } catch (err) {
        console.error('Erro ao carregar professores/escolas:', err);
      }
    };

    fetchAtividade();
    fetchProfessoresEscolas();
  }, [id]);

  // Autosave
  useEffect(() => {
    const autosaveTimer = setTimeout(() => {
      if (titulo && (descricao || editorContent) && !saving) {
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
      const atividadeData = { 
        titulo, 
        tipoConteudo, 
        conteudoTexto: editorContent,
        ...(user?.role === 'ADMIN' && { professorId, escolaId })
      };
      formData.append('atividade', new Blob([JSON.stringify(atividadeData)], { type: 'application/json' }));
      
      if (arquivo && tipoConteudo === 'ARQUIVO_UPLOAD') {
        formData.append('arquivo', arquivo);
      }

      await axiosConfig.put(`/atividades/${id}`, formData, {
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
    if (!titulo.trim()) {
      showSnackbar('Título é obrigatório', 'error');
      return;
    }

    setLoading(true);
    try {
      const formData = new FormData();
      const atividadeData = { 
        titulo, 
        tipoConteudo, 
        conteudoTexto: editorContent,
        ...(user?.role === 'ADMIN' && { professorId, escolaId })
      };
      formData.append('atividade', new Blob([JSON.stringify(atividadeData)], { type: 'application/json' }));

      if (tipoConteudo === 'ARQUIVO_UPLOAD' && arquivo) {
        formData.append('arquivo', arquivo);
      }

      await axiosConfig.put(`/atividades/${id}`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
        onUploadProgress: (progressEvent) => {
          const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          setUploadProgress(progress);
        }
      });

      showSnackbar('Atividade atualizada com sucesso!', 'success');
      setTimeout(() => navigate('/professor/atividades'), 1500);
    } catch (err) {
      showSnackbar('Erro ao atualizar a atividade. ' + (err.response?.data?.message || err.message), 'error');
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
      {(arquivo || nomeArquivoOriginal) && (
        <Box sx={{ mt: 2, p: 2, bgcolor: 'grey.50', borderRadius: 1 }}>
          <Typography variant="subtitle2" gutterBottom>Anexo:</Typography>
          <Chip 
            icon={<AttachFile />} 
            label={arquivo ? arquivo.name : nomeArquivoOriginal} 
            variant="outlined"
            onDelete={arquivo ? () => setArquivo(null) : undefined}
          />
        </Box>
      )}
    </Paper>
  );

  // Função para alternar compartilhamento
  const handleToggleCompartilhada = async (e) => {
    const checked = e.target.checked;
    setCompartilhada(checked);
    setErroRemoverCompartilhamento('');
    try {
      if (checked) {
        await axiosConfig.post(`/atividades/compartilhadas/compartilhar/${id}`, { atividadeId: Number(id) });
        showSnackbar('Atividade compartilhada com sucesso!', 'success');
      } else {
        await axiosConfig.delete(`/atividades/compartilhadas/remover/${id}`);
        showSnackbar('Compartilhamento removido!', 'info');
      }
    } catch (err) {
      if (err.response?.data?.message?.includes('atividade_favorita')) {
        setErroFavorito(true);
      } else if (err.response?.status === 403) {
        setErroRemoverCompartilhamento('Você não tem permissão para remover este compartilhamento. Caso acredite ser um erro, contate o administrador.');
      } else if (err.response?.status === 404) {
        setErroRemoverCompartilhamento('Compartilhamento não encontrado. Ele pode já ter sido removido.');
      } else if (err.response?.status === 500) {
        setErroRemoverCompartilhamento('Ocorreu um erro interno ao tentar remover o compartilhamento. Por favor, tente novamente mais tarde ou contate o suporte.');
      } else {
        setErroRemoverCompartilhamento('Erro ao alterar compartilhamento: ' + (err.response?.data?.message || err.message));
      }
      setCompartilhada(!checked); // Reverte visualmente
    }
  };

  // Substitua a função formatarTexto por uma que gera parágrafos HTML
  const embelezarTexto = () => {
    let texto = editorContent;
    // Remove todas as tags HTML e converte quebras de linha em espaço
    texto = texto.replace(/<[^>]+>/g, '').replace(/\n+/g, ' ');
    // Quebra por ponto final, interrogação, exclamação OU por quebra de linha
    const paragrafos = texto.split(/(?<=[.!?])\s+|\n+/g).map(p => p.trim()).filter(Boolean);
    const html = paragrafos.map(p => `<p>${p}</p>`).join('');
    setEditorContent(html);
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ minHeight: '100vh', bgcolor: 'grey.50' }}>
      {/* Header */}
      <AppBar position="static" color="primary" elevation={0}>
        <Toolbar>
          <IconButton 
            color="inherit" 
            onClick={() => navigate('/professor/atividades')}
            sx={{ mr: 2 }}
          >
            <ArrowBack />
          </IconButton>
          <Assignment sx={{ mr: 2 }} />
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            {titulo ? `Editando: ${titulo}` : 'Editar Atividade'}
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
              {loading ? 'Salvando...' : 'Salvar Alterações'}
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

                {/* Campo de seleção de professor - apenas para ADMIN */}
                {user?.role === 'ADMIN' && (
                  <FormControl fullWidth margin="normal" size="small">
                    <InputLabel>Professor</InputLabel>
                    <Select
                      value={professorId}
                      label="Professor"
                      onChange={(e) => setProfessorId(e.target.value)}
                    >
                      {professores.map(prof => (
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
                      {escolas.map(esc => (
                        <MenuItem key={esc.id} value={esc.id}>
                          {esc.nome}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                )}

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
                    {(arquivo || nomeArquivoOriginal) && (
                      <Chip 
                        icon={<AttachFile />} 
                        label={arquivo ? arquivo.name : nomeArquivoOriginal} 
                        onDelete={arquivo ? () => setArquivo(null) : undefined}
                        sx={{ mt: 1, width: '100%' }}
                      />
                    )}
                  </Box>
                )}

                <Divider sx={{ my: 2 }} />
                <FormControlLabel
                  control={<Switch checked={compartilhada} onChange={handleToggleCompartilhada} color="primary" />}
                  label={compartilhada ? 'Atividade compartilhada' : 'Atividade privada'}
                  sx={{ mb: 2 }}
                />
                
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
                <Button variant="outlined" color="primary" sx={{ mt: 2, mb: 2 }} onClick={embelezarTexto}>
                  Embelezar Texto
                </Button>
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

      {erroFavorito && (
        <Alert severity="error" sx={{ my: 2 }}>
          Não é possível alterar ou remover uma atividade que está favoritada por algum usuário. Peça para remover dos favoritos antes de tentar novamente.
        </Alert>
      )}

      {erroRemoverCompartilhamento && (
        <Alert severity="error" sx={{ my: 2, fontSize: '1rem', fontWeight: 500, border: '1px solid #f44336', background: '#fff0f0' }}>
          {erroRemoverCompartilhamento}
        </Alert>
      )}
    </Box>
  );
};

export default EditarAtividade; 