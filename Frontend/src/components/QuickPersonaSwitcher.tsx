import React from 'react';
import { useAuth, DEMO_PERSONAS } from '../context/AuthContext';
import { Role } from '../types';
import { ShieldCheck, Edit3, CheckSquare, Eye, Sparkles } from 'lucide-react';

interface QuickPersonaSwitcherProps {
  compact?: boolean;
}

export const QuickPersonaSwitcher: React.FC<QuickPersonaSwitcherProps> = ({ compact = false }) => {
  const { role, switchPersona, isLoading } = useAuth();

  const getRoleIcon = (r: Role) => {
    switch (r) {
      case 'ADMIN':
        return <ShieldCheck size={14} className="text-amber-400" />;
      case 'EDITOR':
        return <Edit3 size={14} className="text-indigo-400" />;
      case 'REVIEWER':
        return <CheckSquare size={14} className="text-emerald-400" />;
      case 'VIEWER':
        return <Eye size={14} className="text-slate-400" />;
    }
  };

  const roles: Role[] = ['ADMIN', 'EDITOR', 'REVIEWER', 'VIEWER'];

  if (compact) {
    return (
      <div className="flex items-center gap-1 bg-slate-100 dark:bg-slate-800 p-1 rounded-lg border border-slate-200 dark:border-slate-700/60">
        {roles.map((r) => {
          const active = role === r;
          return (
            <button
              key={r}
              onClick={() => switchPersona(r)}
              disabled={isLoading || active}
              title={`Switch persona to ${DEMO_PERSONAS[r].name} (${r})`}
              className={`flex items-center gap-1.5 px-2.5 py-1 rounded-md text-xs font-semibold transition-all ${
                active
                  ? 'btn-corporate-primary shadow-sm'
                  : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200 hover:bg-slate-200/60 dark:hover:bg-slate-700/50'
              }`}
            >
              {getRoleIcon(r)}
              <span className="hidden sm:inline">{r}</span>
            </button>
          );
        })}
      </div>
    );
  }

  return (
    <div className="card-corporate p-5">
      <div className="flex items-center justify-between mb-3.5">
        <div className="flex items-center gap-2">
          <div className="p-1 rounded-md bg-amber-50 dark:bg-amber-950/50 text-amber-600 dark:text-amber-400">
            <Sparkles size={16} />
          </div>
          <h4 className="text-xs font-bold uppercase tracking-wider text-slate-800 dark:text-slate-200">
            Interactive Persona Switcher (RBAC Demo)
          </h4>
        </div>
        <span className="text-[11px] font-semibold text-indigo-600 dark:text-indigo-400 font-mono">1-Click Live Switch</span>
      </div>

      <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
        {roles.map((r) => {
          const persona = DEMO_PERSONAS[r];
          const active = role === r;

          return (
            <button
              key={r}
              onClick={() => switchPersona(r)}
              disabled={isLoading || active}
              className={`group relative flex flex-col p-3.5 rounded-xl border text-left transition-all ${
                active
                  ? 'bg-indigo-50/90 dark:bg-indigo-950/60 border-indigo-300 dark:border-indigo-700 shadow-corporate'
                  : 'bg-white dark:bg-slate-800/60 border-slate-200 dark:border-slate-700 hover:border-indigo-200 hover:bg-slate-50 dark:hover:bg-slate-800'
              }`}
            >
              <div className="flex items-center justify-between w-full mb-1.5">
                <span className="flex items-center gap-1.5 text-xs font-bold tracking-wide">
                  {getRoleIcon(r)}
                  <span className={active ? 'text-indigo-900 dark:text-indigo-200' : 'text-slate-700 dark:text-slate-300'}>{r}</span>
                </span>
                {active && (
                  <span className="w-2 h-2 rounded-full bg-emerald-500 ring-4 ring-emerald-100 dark:ring-emerald-900/50" />
                )}
              </div>
              <span className="text-xs font-bold text-slate-900 dark:text-slate-100 truncate">{persona.name}</span>
              <span className="text-[11px] text-slate-500 dark:text-slate-400 truncate">{persona.title}</span>
            </button>
          );
        })}
      </div>
    </div>
  );
};
