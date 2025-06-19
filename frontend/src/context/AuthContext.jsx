import React, { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de um AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Verificar se há dados de usuário no localStorage ao carregar a página
    const email = localStorage.getItem('email');
    const role = localStorage.getItem('role');
    const id = localStorage.getItem('id');

    if (email && role && id) {
      setUser({ email, role, id });
    }
    setLoading(false);
  }, []);

  const login = (userData) => {
    const { email, role, id } = userData;
    localStorage.setItem('email', email);
    localStorage.setItem('role', role);
    localStorage.setItem('id', id);
    setUser({ email, role, id });
  };

  const logout = () => {
    localStorage.removeItem('email');
    localStorage.removeItem('role');
    localStorage.removeItem('id');
    setUser(null);
  };

  const value = {
    user,
    login,
    logout,
    loading
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}; 