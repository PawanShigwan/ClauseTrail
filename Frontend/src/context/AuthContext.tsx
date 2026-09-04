import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/client';
import { User, Role, AuthResponse } from '../types';

interface AuthContextType {
  user: User | null;
  token: string | null;
  role: Role | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (name: string, email: string, password: string, role: Role, department?: string, title?: string) => Promise<void>;
  logout: () => void;
  switchPersona: (targetRole: Role) => Promise<void>;
  refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const DEMO_PERSONAS: Record<Role, { email: string; name: string; title: string; avatarColor: string }> = {
  ADMIN: {
    email: 'admin@clausetrail.com',
    name: 'Eleanor Vance',
    title: 'Head of Legal Operations & Admin',
    avatarColor: 'from-amber-500 to-orange-600',
  },
  EDITOR: {
    email: 'editor@clausetrail.com',
    name: 'Marcus Reed',
    title: 'Senior Contract Editor',
    avatarColor: 'from-indigo-500 to-blue-600',
  },
  REVIEWER: {
    email: 'reviewer@clausetrail.com',
    name: 'Sarah Jenkins',
    title: 'Managing Partner & Approver',
    avatarColor: 'from-emerald-500 to-teal-600',
  },
  VIEWER: {
    email: 'viewer@clausetrail.com',
    name: 'David Kim',
    title: 'Procurement Auditor (Read-only)',
    avatarColor: 'from-slate-500 to-gray-600',
  },
};

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('clausetrail_jwt'));
  const [isLoading, setIsLoading] = useState<boolean>(true);

  const refreshUser = async () => {
    try {
      if (!localStorage.getItem('clausetrail_jwt')) {
        setUser(null);
        setIsLoading(false);
        return;
      }
      const res = await api.get<User>('/auth/me');
      setUser(res.data);
    } catch (err) {
      console.warn('Failed to fetch current user profile:', err);
      logout();
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    refreshUser();
  }, []);

  const login = async (email: string, password: string) => {
    setIsLoading(true);
    try {
      const res = await api.post<AuthResponse>('/auth/login', { email, password });
      const authData = res.data;
      localStorage.setItem('clausetrail_jwt', authData.token);
      setToken(authData.token);
      setUser({
        id: authData.id,
        name: authData.name,
        email: authData.email,
        role: authData.role,
        department: authData.department,
        title: authData.title,
        active: true,
        createdAt: new Date().toISOString(),
      });
    } finally {
      setIsLoading(false);
    }
  };

  const register = async (name: string, email: string, password: string, role: Role, department?: string, title?: string) => {
    setIsLoading(true);
    try {
      const res = await api.post<AuthResponse>('/auth/register', { name, email, password, role, department, title });
      const authData = res.data;
      localStorage.setItem('clausetrail_jwt', authData.token);
      setToken(authData.token);
      setUser({
        id: authData.id,
        name: authData.name,
        email: authData.email,
        role: authData.role,
        department: authData.department,
        title: authData.title,
        active: true,
        createdAt: new Date().toISOString(),
      });
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem('clausetrail_jwt');
    setToken(null);
    setUser(null);
  };

  const switchPersona = async (targetRole: Role) => {
    const persona = DEMO_PERSONAS[targetRole];
    if (persona) {
      await login(persona.email, 'password123');
    }
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        role: user ? user.role : null,
        isAuthenticated: !!user && !!token,
        isLoading,
        login,
        register,
        logout,
        switchPersona,
        refreshUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
