import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default api;

export const favoritarAtividade = (professorId, atividadeCompartilhadaId) =>
  api.post(`/atividades/favoritas`, null, { params: { professorId, atividadeCompartilhadaId } });

export const desfavoritarAtividade = (professorId, atividadeCompartilhadaId) =>
  api.delete(`/atividades/favoritas`, { params: { professorId, atividadeCompartilhadaId } });

export const listarFavoritas = (professorId) =>
  api.get(`/atividades/favoritas/professor/${professorId}`); 