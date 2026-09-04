import React, { useEffect, useState } from 'react';
import { usersApi } from '../api/client';
import { User, Role } from '../types';
import { useAuth } from '../context/AuthContext';
import { Users, Shield, Edit, Check, AlertCircle, Sparkles } from 'lucide-react';

const ROLES: Role[] = ['ADMIN', 'EDITOR', 'REVIEWER', 'VIEWER'];

export default function AdminUserManagementView() {
  const { user: currentUser } = useAuth();
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [updatingId, setUpdatingId] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState('');
  const [errorMsg, setErrorMsg] = useState('');

  const loadUsers = async () => {
    setLoading(true);
    try {
      const { data } = await usersApi.getAll();
      setUsers(data);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadUsers();
  }, []);

  const handleRoleChange = async (userId: string, newRole: Role) => {
    setUpdatingId(userId);
    setSuccessMsg('');
    setErrorMsg('');
    try {
      await usersApi.updateRole(userId, newRole);
      setSuccessMsg(`Role updated to ${newRole} successfully.`);
      loadUsers();
    } catch (err: any) {
      setErrorMsg(err?.response?.data?.message || 'Failed to update user role.');
    } finally {
      setUpdatingId(null);
    }
  };

  const getRoleColor = (role: Role) => {
    switch (role) {
      case 'ADMIN': return '#a78bfa';
      case 'EDITOR': return '#34d399';
      case 'REVIEWER': return '#60a5fa';
      case 'VIEWER': return '#fb923c';
    }
  };

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-6xl mx-auto space-y-6">
      {/* Header */}
      <div className="space-y-1">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-indigo-600 to-violet-600 text-white flex items-center justify-center shadow-corporate-btn">
            <Users size={20} />
          </div>
          <div>
            <h1 className="text-xl sm:text-2xl font-extrabold text-slate-900 dark:text-white tracking-tight">
              User Management & <span className="gradient-text">RBAC</span>
            </h1>
            <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400">
              Manage organizational team members, assign RBAC roles, and govern permissions.
            </p>
          </div>
        </div>
      </div>

      {successMsg && (
        <div className="p-3.5 rounded-lg bg-emerald-50 dark:bg-emerald-950/50 border border-emerald-200 dark:border-emerald-800 text-emerald-800 dark:text-emerald-300 text-xs font-semibold flex items-center gap-2">
          <Check size={16} />
          <span>{successMsg}</span>
        </div>
      )}

      {errorMsg && (
        <div className="p-3.5 rounded-lg bg-rose-50 dark:bg-rose-950/50 border border-rose-200 dark:border-rose-800 text-rose-800 dark:text-rose-300 text-xs font-semibold flex items-center gap-2">
          <AlertCircle size={16} />
          <span>{errorMsg}</span>
        </div>
      )}

      {/* Users Card */}
      <div className="card-corporate overflow-hidden">
        <div className="px-5 py-4 border-b border-slate-100 dark:border-slate-800 bg-slate-50/60 dark:bg-slate-800/40 flex items-center justify-between">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-800 dark:text-slate-200">
            Platform Users ({users.length})
          </span>
          <span className="text-[11px] text-slate-400">Role changes apply immediately</span>
        </div>

        {loading ? (
          <div className="py-16 text-center text-slate-400">
            <div className="w-8 h-8 border-2 border-indigo-600 border-t-transparent rounded-full animate-spin mx-auto mb-3" />
            <p className="text-xs font-medium">Loading platform directory...</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-slate-200 dark:border-slate-800 text-[11px] font-bold text-slate-400 uppercase tracking-wider bg-slate-50/30 dark:bg-slate-900/30">
                  <th className="px-5 py-3">User & Email</th>
                  <th className="px-5 py-3">Department & Title</th>
                  <th className="px-5 py-3">Current Role</th>
                  <th className="px-5 py-3 text-right">Change Role</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800/60 text-xs">
                {users.map((u) => {
                  const isSelf = u.id === currentUser?.id;
                  const roleBgMap: Record<Role, string> = {
                    ADMIN: 'bg-purple-50 dark:bg-purple-950/60 text-purple-700 dark:text-purple-300 border-purple-200 dark:border-purple-800',
                    EDITOR: 'bg-emerald-50 dark:bg-emerald-950/60 text-emerald-700 dark:text-emerald-300 border-emerald-200 dark:border-emerald-800',
                    REVIEWER: 'bg-blue-50 dark:bg-blue-950/60 text-blue-700 dark:text-blue-300 border-blue-200 dark:border-blue-800',
                    VIEWER: 'bg-amber-50 dark:bg-amber-950/60 text-amber-700 dark:text-amber-300 border-amber-200 dark:border-amber-800',
                  };

                  return (
                    <tr
                      key={u.id}
                      className="hover:bg-indigo-50/30 dark:hover:bg-indigo-950/10 transition-colors"
                    >
                      <td className="px-5 py-3.5">
                        <div className="font-bold text-slate-900 dark:text-slate-100 flex items-center gap-1.5">
                          <span>{u.name}</span>
                          {isSelf && (
                            <span className="px-1.5 py-0.5 rounded text-[10px] font-bold bg-indigo-50 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-800">
                              You
                            </span>
                          )}
                        </div>
                        <div className="text-[11px] text-slate-500 dark:text-slate-400 mt-0.5">{u.email}</div>
                      </td>

                      <td className="px-5 py-3.5">
                        <div className="font-medium text-slate-800 dark:text-slate-200">{u.department || 'Legal Operations'}</div>
                        <div className="text-[11px] text-slate-400">{u.title || 'Counsel'}</div>
                      </td>

                      <td className="px-5 py-3.5">
                        <span className={`inline-block px-2.5 py-1 rounded-full text-[11px] font-bold border ${roleBgMap[u.role]}`}>
                          {u.role}
                        </span>
                      </td>

                      <td className="px-5 py-3.5 text-right">
                        <select
                          value={u.role}
                          disabled={updatingId === u.id || isSelf}
                          onChange={(e) => handleRoleChange(u.id, e.target.value as Role)}
                          className="px-3 py-1.5 text-xs font-semibold input-corporate w-36 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                          {ROLES.map((r) => (
                            <option key={r} value={r}>
                              {r}
                            </option>
                          ))}
                        </select>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
