import axios from 'axios';
import {
  AuthResponse, User, Contract, ContractVersion, DiffResponse,
  ReviewQueueItem, AuditLog, NotificationItem, Role, VersionStatus, ClauseDTO, ClauseChangeDTO
} from '../types';

// Resolve the API base URL at build time:
//   - Production (Vercel): set VITE_API_URL to your Render backend URL
//     e.g. https://clausetrail-backend.onrender.com
//   - Local development: leave VITE_API_URL unset; the Vite proxy in
//     vite.config.ts routes /api → http://localhost:8080 automatically.
const API_BASE_URL = import.meta.env.VITE_API_URL
  ? `${import.meta.env.VITE_API_URL}/api`
  : '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('clausetrail_jwt');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      console.warn('Session expired or unauthorized');
    }
    return Promise.reject(error);
  }
);

export const authApi = {
  login: (email: string, password: string) =>
    api.post<AuthResponse>('/auth/login', { email, password }),
  register: (name: string, email: string, password: string, department?: string, title?: string, role?: Role) =>
    api.post<AuthResponse>('/auth/register', { name, email, password, department, title, role }),
  getCurrentUser: () =>
    api.get<User>('/auth/me'),
};

export const contractsApi = {
  getAll: (params?: { search?: string; status?: string; contractType?: string }) =>
    api.get<Contract[]>('/contracts', { params }),
  getById: (id: string, includeFullVersions = true) =>
    api.get<Contract>(`/contracts/${id}`, { params: { includeFullVersions } }),
  create: (data: {
    title: string;
    contractType: string;
    parties: string[];
    description?: string;
    tags?: string[];
    effectiveDate?: string;
    expirationDate?: string;
    contractValue?: string;
    fullText?: string;
    clauses?: ClauseDTO[];
  }) => api.post<Contract>('/contracts', data),
  upload: (formData: FormData) =>
    api.post<Contract>('/contracts/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }),
  modify: (id: string, data: { modificationReason: string; clauseChanges: ClauseChangeDTO[] }) =>
    api.post<Contract>(`/contracts/${id}/modify`, data),
  getVersionHistory: (id: string) =>
    api.get<ContractVersion[]>(`/contracts/${id}/versions`),
  getVersionByNumber: (id: string, versionNumber: number) =>
    api.get<ContractVersion>(`/contracts/${id}/versions/${versionNumber}`),
  getDiff: (id: string, v1: number, v2: number) =>
    api.get<DiffResponse>(`/contracts/${id}/diff/${v1}/${v2}`),
  exportPdf: (id: string, version?: number) =>
    api.get(`/contracts/${id}/export-pdf`, {
      params: { version },
      responseType: 'blob',
    }),
};

export const reviewApi = {
  getReviewQueue: () =>
    api.get<ReviewQueueItem[]>('/review-queue'),
  reviewContract: (id: string, data: { action: VersionStatus; reviewComments?: string }) =>
    api.post<Contract>(`/contracts/${id}/review`, data),
};

export const usersApi = {
  getAll: () =>
    api.get<User[]>('/users'),
  getById: (id: string) =>
    api.get<User>(`/users/${id}`),
  updateRole: (id: string, role: Role) =>
    api.put<User>(`/users/${id}/role`, { role }),
};

export const auditApi = {
  getLogs: (params?: { contractId?: string; action?: string }) =>
    api.get<AuditLog[]>('/audit-logs', { params }),
  exportCsv: (contractId?: string) =>
    api.get('/audit-logs/export', {
      params: { contractId },
      responseType: 'blob',
    }),
};

export const notificationsApi = {
  getNotifications: () =>
    api.get<NotificationItem[]>('/notifications'),
  getUnreadCount: () =>
    api.get<{ count: number }>('/notifications/unread-count'),
  markAsRead: (id: string) =>
    api.put<NotificationItem>(`/notifications/${id}/read`),
  markAllAsRead: () =>
    api.put<{ message: string }>('/notifications/read-all'),
};

export default api;
