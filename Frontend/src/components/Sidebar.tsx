import React from 'react';
import { useAuth } from '../context/AuthContext';
import { useNotifications } from '../context/NotificationContext';
import {
  LayoutDashboard,
  FileText,
  CheckSquare,
  ShieldAlert,
  Users,
  PlusCircle,
  Sparkles
} from 'lucide-react';

interface SidebarProps {
  currentView: string;
  onNavigate: (view: string, id?: string) => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ currentView, onNavigate }) => {
  const { role } = useAuth();
  const { unreadCount } = useNotifications();

  const navItems = [
    {
      id: 'dashboard',
      label: 'Contracts Dashboard',
      icon: LayoutDashboard,
      roles: ['ADMIN', 'EDITOR', 'REVIEWER', 'VIEWER'],
    },
    {
      id: 'review-queue',
      label: 'Review Queue',
      icon: CheckSquare,
      roles: ['ADMIN', 'REVIEWER'],
    },
    {
      id: 'audit-logs',
      label: 'Audit Trail',
      icon: ShieldAlert,
      roles: ['ADMIN', 'REVIEWER'],
    },
    {
      id: 'users',
      label: 'User Directory',
      icon: Users,
      roles: ['ADMIN'],
    },
  ];

  return (
    <aside className="w-64 bg-white/70 dark:bg-slate-900/70 backdrop-blur-md border-r border-slate-200/90 dark:border-slate-800/80 min-h-[calc(100vh-4rem)] p-4 flex flex-col justify-between hidden lg:flex transition-colors">
      <div>
        {/* New Contract Action Button for Editor/Admin */}
        {(role === 'EDITOR' || role === 'ADMIN') && (
          <button
            onClick={() => onNavigate('new-contract')}
            className="w-full mb-6 flex items-center justify-center gap-2 py-2.5 px-4 rounded-lg btn-corporate-primary text-xs font-semibold"
          >
            <PlusCircle size={16} />
            <span>Create New Contract</span>
          </button>
        )}

        {/* Navigation Items */}
        <div className="space-y-1">
          <p className="px-3 text-[11px] font-bold uppercase tracking-wider text-slate-400 dark:text-slate-400 mb-2.5">
            Main Navigation
          </p>
          {navItems
            .filter((item) => !item.roles || (role && item.roles.includes(role)))
            .map((item) => {
              const Icon = item.icon;
              const active = currentView === item.id;
              return (
                <button
                  key={item.id}
                  onClick={() => onNavigate(item.id)}
                  className={`w-full flex items-center justify-between px-3.5 py-2.5 rounded-lg text-xs font-semibold transition-all duration-200 ${
                    active
                      ? 'bg-indigo-50 dark:bg-indigo-950/70 text-indigo-700 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-800/80 shadow-[0_2px_10px_-2px_rgba(79,70,229,0.12)]'
                      : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-100 hover:bg-slate-100/80 dark:hover:bg-slate-800/60 border border-transparent'
                  }`}
                >
                  <div className="flex items-center gap-3">
                    <div className={`p-1 rounded-md ${active ? 'bg-indigo-600 text-white' : 'text-slate-500 dark:text-slate-400'}`}>
                      <Icon size={15} />
                    </div>
                    <span>{item.label}</span>
                  </div>
                </button>
              );
            })}
        </div>
      </div>

      {/* Footer info card */}
      <div className="p-4 rounded-xl bg-gradient-to-br from-indigo-50/80 to-violet-50/80 dark:from-slate-800/60 dark:to-slate-800/40 border border-indigo-100 dark:border-slate-700/60 text-[11px] text-slate-600 dark:text-slate-400 space-y-1.5 shadow-[0_2px_10px_rgba(79,70,229,0.05)]">
        <div className="flex items-center gap-1.5 text-indigo-900 dark:text-indigo-200 font-bold">
          <Sparkles size={13} className="text-indigo-600 dark:text-indigo-400" />
          <span>Enterprise Integrity</span>
        </div>
        <p className="text-[10px] leading-relaxed text-slate-500 dark:text-slate-400">
          Strict RBAC, immutable MongoDB audit trails & word-level diff verification.
        </p>
      </div>
    </aside>
  );
};

export default Sidebar;
