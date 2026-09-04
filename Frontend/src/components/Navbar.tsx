import React, { useState, useEffect } from 'react';
import { useAuth, DEMO_PERSONAS } from '../context/AuthContext';
import { QuickPersonaSwitcher } from './QuickPersonaSwitcher';
import { NotificationDropdown } from './NotificationDropdown';
import { FileText, LogOut, Shield, Sparkles, Sun, Moon } from 'lucide-react';

interface NavbarProps {
  currentView: string;
  onNavigate: (view: string, id?: string) => void;
}

export const Navbar: React.FC<NavbarProps> = ({ onNavigate }) => {
  const { user, role, logout } = useAuth();
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [isDark, setIsDark] = useState(false);

  useEffect(() => {
    // Check initial dark mode state
    setIsDark(document.documentElement.classList.contains('dark'));
  }, []);

  const toggleTheme = () => {
    const nextDark = !isDark;
    setIsDark(nextDark);
    if (nextDark) {
      document.documentElement.classList.add('dark');
      localStorage.setItem('theme', 'dark');
    } else {
      document.documentElement.classList.remove('dark');
      localStorage.setItem('theme', 'light');
    }
  };

  const persona = role ? DEMO_PERSONAS[role] : null;

  return (
    <header className="sticky top-0 z-40 w-full bg-white/90 dark:bg-slate-900/90 backdrop-blur-md border-b border-slate-200/90 dark:border-slate-800/80 shadow-[0_2px_15px_-3px_rgba(79,70,229,0.06)] transition-colors">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between gap-4">
        {/* Left: Brand Logo */}
        <div className="flex items-center gap-3 cursor-pointer group" onClick={() => onNavigate('dashboard')}>
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-indigo-600 to-violet-600 flex items-center justify-center shadow-corporate-btn group-hover:scale-105 transition-transform duration-200">
            <FileText className="text-white w-5 h-5" />
          </div>
          <div>
            <div className="flex items-center gap-1.5">
              <span className="font-extrabold text-lg tracking-tight text-slate-900 dark:text-white font-sans">
                Clause<span className="gradient-text">Trail</span>
              </span>
              <span className="text-[10px] px-2 py-0.5 rounded-full bg-indigo-50 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 font-mono font-semibold border border-indigo-200 dark:border-indigo-800">
                Enterprise
              </span>
            </div>
            <p className="text-[11px] text-slate-500 dark:text-slate-400 font-medium tracking-tight">Legal Contract Modification Platform</p>
          </div>
        </div>

        {/* Right: Actions, Persona Switcher, Theme Toggle, Notifications, Profile */}
        <div className="flex items-center gap-2 sm:gap-3">
          {/* Theme Switcher */}
          <button
            onClick={toggleTheme}
            aria-label="Toggle theme"
            className="p-2 rounded-lg text-slate-500 hover:text-indigo-600 hover:bg-slate-100 dark:text-slate-400 dark:hover:text-indigo-300 dark:hover:bg-slate-800 transition-colors"
            title={isDark ? 'Switch to Corporate Trust Light Mode' : 'Switch to Dark Mode'}
          >
            {isDark ? <Sun size={18} className="text-amber-400" /> : <Moon size={18} />}
          </button>

          {/* Quick Persona Switcher in nav */}
          <div className="hidden sm:block">
            <QuickPersonaSwitcher compact />
          </div>

          {/* New Contract Button (Editor or Admin) */}
          {(role === 'EDITOR' || role === 'ADMIN') && (
            <button
              onClick={() => onNavigate('new-contract')}
              className="hidden lg:flex items-center gap-1.5 px-3.5 py-2 text-xs font-semibold rounded-lg btn-corporate-primary"
            >
              <Sparkles size={14} />
              <span>+ New Contract</span>
            </button>
          )}

          {/* Notification Bell */}
          <NotificationDropdown onSelectContract={(id) => onNavigate('contract', id)} />

          {/* User Profile */}
          <div className="relative">
            <button
              onClick={() => setShowUserMenu(!showUserMenu)}
              className="flex items-center gap-2 p-1.5 rounded-xl hover:bg-slate-100 dark:hover:bg-slate-800/80 transition-colors border border-transparent hover:border-slate-200 dark:hover:border-slate-700"
            >
              <div className={`w-8 h-8 rounded-lg bg-gradient-to-br ${persona?.avatarColor || 'from-indigo-600 to-violet-600'} flex items-center justify-center text-white font-bold text-xs shadow-corporate-btn`}>
                {user?.name ? user.name.charAt(0) : 'U'}
              </div>
              <div className="hidden xl:block text-left">
                <p className="text-xs font-semibold text-slate-800 dark:text-slate-200 truncate max-w-[120px]">{user?.name}</p>
                <p className="text-[10px] font-medium text-indigo-600 dark:text-indigo-400 capitalize">{role?.toLowerCase()}</p>
              </div>
            </button>

            {showUserMenu && (
              <div className="absolute right-0 mt-2 w-60 rounded-xl bg-white dark:bg-slate-900 shadow-corporate-hover border border-slate-200 dark:border-slate-800 py-2 z-50 animate-fade-in">
                <div className="px-4 py-2.5 border-b border-slate-100 dark:border-slate-800">
                  <p className="text-xs font-bold text-slate-900 dark:text-slate-100">{user?.name}</p>
                  <p className="text-[11px] text-slate-500 dark:text-slate-400 truncate">{user?.email}</p>
                  <div className="mt-2 inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full bg-indigo-50 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 text-[10px] font-semibold border border-indigo-200 dark:border-indigo-800">
                    <Shield size={11} />
                    <span>Role: {role}</span>
                  </div>
                </div>

                <div className="p-1.5">
                  <button
                    onClick={() => {
                      setShowUserMenu(false);
                      logout();
                    }}
                    className="w-full flex items-center gap-2 px-3 py-2 text-xs font-medium text-rose-600 dark:text-rose-400 hover:bg-rose-50 dark:hover:bg-rose-500/10 rounded-lg transition-colors"
                  >
                    <LogOut size={14} />
                    <span>Sign Out</span>
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};

export default Navbar;
