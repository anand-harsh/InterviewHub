'use client';

import React, { createContext, useContext, useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import api from '../../lib/api';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const router = useRouter();

  useEffect(() => {
    checkAuth();
  }, []);

  const checkAuth = async () => {
    try {
      const token = localStorage.getItem('accessToken');
      if (token) {
        const response = await api.getProfile();
        if (response.data) {
          const userData = {
            id: response.data.userId,
            email: response.data.email || '',
            emailVerified: true,
            profileCompleted: true,
            lastLogin: new Date().toISOString()
          };
          setUser(userData);
        }
      }
    } catch (error) {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
    } finally {
      setLoading(false);
    }
  };

  const login = async (credentials) => {
    const response = await api.login(credentials);
    if (response.data) {
      setUser(response.data.user);
    }
  };

  const register = async (data) => {
    const response = await api.register(data);
    if (response.success) {
      await login({ email: data.email, password: data.password });
    }
  };

  const logout = async () => {
    await api.logout();
    setUser(null);
    router.push('/login');
  };

  const refreshUser = async () => {
    await checkAuth();
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout, refreshUser }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};