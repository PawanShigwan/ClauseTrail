import React, { useState } from 'react';
import { FileText, Lock, Eye, EyeOff, Zap, Shield, GitBranch, Users, Sparkles, CheckCircle2, ArrowRight, ShieldCheck } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const DEMO_ACCOUNTS = [
  {
    label: 'Admin',
    name: 'Eleanor Vance',
    email: 'admin@clausetrail.com',
    password: 'password123',
    role: 'ADMIN',
    icon: ShieldCheck,
    border: 'border-violet-200 dark:border-violet-800',
    bg: 'bg-violet-50/80 dark:bg-violet-950/40 text-violet-700 dark:text-violet-300',
    desc: 'System governance & roles'
  },
  {
    label: 'Editor',
    name: 'Marcus Reed',
    email: 'editor@clausetrail.com',
    password: 'password123',
    role: 'EDITOR',
    icon: GitBranch,
    border: 'border-indigo-200 dark:border-indigo-800',
    bg: 'bg-indigo-50/80 dark:bg-indigo-950/40 text-indigo-700 dark:text-indigo-300',
    desc: 'Propose clause modifications'
  },
  {
    label: 'Reviewer',
    name: 'Sarah Jenkins',
    email: 'reviewer@clausetrail.com',
    password: 'password123',
    role: 'REVIEWER',
    icon: Eye,
    border: 'border-emerald-200 dark:border-emerald-800',
    bg: 'bg-emerald-50/80 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300',
    desc: 'Approve/reject & diff studio'
  },
  {
    label: 'Viewer',
    name: 'David Kim',
    email: 'viewer@clausetrail.com',
    password: 'password123',
    role: 'VIEWER',
    icon: Users,
    border: 'border-slate-200 dark:border-slate-700',
    bg: 'bg-slate-50 dark:bg-slate-800 text-slate-700 dark:text-slate-300',
    desc: 'Read-only compliance audit'
  }
];

export default function AuthView() {
  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [department, setDepartment] = useState('');
  const [showPass, setShowPass] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const { login, register } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      if (mode === 'login') {
        await login(email, password);
      } else {
        await register(name, email, password, 'EDITOR', department, 'Legal Counsel');
      }
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Authentication failed. Please check credentials or ensure backend is running.');
    } finally {
      setLoading(false);
    }
  };

  const loginAs = async (acc: typeof DEMO_ACCOUNTS[0]) => {
    setError('');
    setLoading(true);
    try {
      await login(acc.email, acc.password);
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Demo login failed. Please ensure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-[#090d16] flex items-center justify-center p-4 sm:p-6 lg:p-12 relative overflow-hidden transition-colors">
      {/* Atmospheric Blurred Orbs */}
      <div className="blob-orb top-[-10%] left-[-5%] w-[550px] h-[550px] bg-gradient-to-br from-indigo-400 to-violet-400 opacity-25 dark:opacity-15" />
      <div className="blob-orb bottom-[-10%] right-[-5%] w-[500px] h-[500px] bg-gradient-to-br from-violet-400 to-indigo-500 opacity-20 dark:opacity-10" />

      <div className="max-w-6xl w-full grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-12 items-center relative z-10">
        
        {/* Left Column: Hero & Isometric 3D Visualization */}
        <div className="lg:col-span-6 space-y-6 text-left">
          {/* Badge */}
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-white dark:bg-slate-900 border border-indigo-100 dark:border-indigo-900/60 shadow-corporate">
            <span className="flex h-2 w-2 rounded-full bg-indigo-600 animate-pulse" />
            <span className="text-xs font-bold gradient-text">Enterprise Legal Intelligence</span>
          </div>

          {/* Split Headline */}
          <h1 className="text-3xl sm:text-4xl lg:text-5xl font-extrabold text-slate-900 dark:text-white leading-[1.15] tracking-tight">
            Trustworthy contract changes, <span className="gradient-text">traceable forever.</span>
          </h1>

          <p className="text-sm sm:text-base text-slate-600 dark:text-slate-400 leading-relaxed max-w-lg">
            ClauseTrail is the modern document modification platform for high-growth legal teams. Track word-level revisions, enforce mandatory justifications, and maintain cryptographic audit trails.
          </p>

          {/* Isometric 3D Card Preview */}
          <div className="perspective-container pt-2 hidden sm:block">
            <div className="isometric-card bg-white dark:bg-slate-900 border border-slate-200/90 dark:border-slate-800 rounded-2xl p-5 shadow-corporate-hover max-w-md">
              <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3 mb-3">
                <div className="flex items-center gap-2">
                  <div className="w-7 h-7 rounded-lg bg-indigo-100 dark:bg-indigo-950/80 flex items-center justify-center text-indigo-600">
                    <FileText size={15} />
                  </div>
                  <span className="text-xs font-bold text-slate-900 dark:text-white truncate max-w-[200px]">
                    Master Cloud Subscription.pdf
                  </span>
                </div>
                <span className="px-2 py-0.5 rounded-full bg-emerald-50 dark:bg-emerald-950/60 text-emerald-700 dark:text-emerald-300 text-[10px] font-bold border border-emerald-200 dark:border-emerald-800">
                  v2.0 Approved
                </span>
              </div>
              
              {/* Diff Snippet */}
              <div className="text-[11px] font-mono p-3 rounded-lg bg-slate-50 dark:bg-slate-950 border border-slate-200/60 dark:border-slate-800/80 space-y-1.5">
                <div className="text-slate-500">Section 4.2 (Liability Cap)</div>
                <div>
                  <span className="diff-deleted">twelve (12) months</span>{' '}
                  <span className="diff-added">twenty-four (24) months</span>
                </div>
                <div className="text-[10px] text-slate-400 pt-1 font-sans">
                  ⚖️ Reason: "Enterprise Risk Committee compliance"
                </div>
              </div>
            </div>
          </div>

          {/* Feature Bullets */}
          <div className="grid grid-cols-2 gap-3 pt-1">
            <div className="flex items-center gap-2 text-xs font-semibold text-slate-700 dark:text-slate-300">
              <CheckCircle2 size={16} className="text-emerald-500 flex-shrink-0" />
              <span>Multi-Version Git-Style Tree</span>
            </div>
            <div className="flex items-center gap-2 text-xs font-semibold text-slate-700 dark:text-slate-300">
              <CheckCircle2 size={16} className="text-emerald-500 flex-shrink-0" />
              <span>Side-by-Side Word Diffing</span>
            </div>
            <div className="flex items-center gap-2 text-xs font-semibold text-slate-700 dark:text-slate-300">
              <CheckCircle2 size={16} className="text-emerald-500 flex-shrink-0" />
              <span>Strict 4-Tier RBAC Guardrails</span>
            </div>
            <div className="flex items-center gap-2 text-xs font-semibold text-slate-700 dark:text-slate-300">
              <CheckCircle2 size={16} className="text-emerald-500 flex-shrink-0" />
              <span>Immutable CSV / PDF Exports</span>
            </div>
          </div>
        </div>

        {/* Right Column: Authentication Card & Demo Personas */}
        <div className="lg:col-span-6 space-y-4">
          <div className="card-corporate p-6 sm:p-8">
            {/* Header / Tabs */}
            <div className="flex items-center justify-between mb-6">
              <div>
                <h2 className="text-xl font-bold text-slate-900 dark:text-white">
                  {mode === 'login' ? 'Welcome Back' : 'Create Account'}
                </h2>
                <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
                  Sign in to access your contract workspace
                </p>
              </div>
              <div className="flex bg-slate-100 dark:bg-slate-800 p-1 rounded-lg border border-slate-200 dark:border-slate-700">
                <button
                  type="button"
                  onClick={() => { setMode('login'); setError(''); }}
                  className={`px-3 py-1 text-xs font-bold rounded-md transition-all ${
                    mode === 'login' ? 'btn-corporate-primary' : 'text-slate-600 dark:text-slate-400'
                  }`}
                >
                  Sign In
                </button>
                <button
                  type="button"
                  onClick={() => { setMode('register'); setError(''); }}
                  className={`px-3 py-1 text-xs font-bold rounded-md transition-all ${
                    mode === 'register' ? 'btn-corporate-primary' : 'text-slate-600 dark:text-slate-400'
                  }`}
                >
                  Register
                </button>
              </div>
            </div>

            {error && (
              <div className="p-3 mb-4 rounded-lg bg-rose-50 dark:bg-rose-950/50 border border-rose-200 dark:border-rose-800 text-rose-700 dark:text-rose-300 text-xs font-medium">
                {error}
              </div>
            )}

            {/* Form */}
            <form onSubmit={handleSubmit} className="space-y-4">
              {mode === 'register' && (
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">Full Name</label>
                    <input
                      type="text"
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      required
                      placeholder="Eleanor Vance"
                      className="w-full px-3.5 py-2.5 text-xs input-corporate"
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">Department</label>
                    <input
                      type="text"
                      value={department}
                      onChange={(e) => setDepartment(e.target.value)}
                      placeholder="Legal Operations"
                      className="w-full px-3.5 py-2.5 text-xs input-corporate"
                    />
                  </div>
                </div>
              )}

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">Email Address</label>
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  placeholder="admin@clausetrail.com"
                  className="w-full px-3.5 py-2.5 text-xs input-corporate"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">Password</label>
                <div className="relative">
                  <input
                    type={showPass ? 'text' : 'password'}
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    placeholder="••••••••"
                    className="w-full px-3.5 py-2.5 text-xs input-corporate pr-10"
                  />
                  <button
                    type="button"
                    onClick={() => setShowPass(!showPass)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200"
                  >
                    {showPass ? <EyeOff size={15} /> : <Eye size={15} />}
                  </button>
                </div>
              </div>

              <button
                type="submit"
                disabled={loading}
                className="w-full py-2.5 px-4 text-xs font-bold rounded-lg btn-corporate-primary flex items-center justify-center gap-2 disabled:opacity-50"
              >
                <span>{loading ? 'Authenticating...' : (mode === 'login' ? 'Sign In to Workspace' : 'Create Enterprise Account')}</span>
                <ArrowRight size={14} />
              </button>
            </form>
          </div>

          {/* Quick Demo Access Bar */}
          <div className="card-corporate p-4">
            <div className="flex items-center gap-2 mb-3">
              <Zap size={15} className="text-amber-500" />
              <span className="text-xs font-bold text-slate-800 dark:text-slate-200 uppercase tracking-wider">
                Instant 1-Click Persona Access
              </span>
            </div>
            
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
              {DEMO_ACCOUNTS.map((acc) => {
                const Icon = acc.icon;
                return (
                  <button
                    key={acc.email}
                    type="button"
                    onClick={() => loginAs(acc)}
                    disabled={loading}
                    className={`p-2.5 rounded-xl border text-left transition-all hover:-translate-y-0.5 hover:shadow-sm ${acc.bg} ${acc.border}`}
                  >
                    <div className="flex items-center gap-1.5 mb-1">
                      <Icon size={13} />
                      <span className="font-bold text-[11px]">{acc.label}</span>
                    </div>
                    <p className="text-[10px] font-medium opacity-80 truncate">{acc.name}</p>
                  </button>
                );
              })}
            </div>
          </div>
        </div>

      </div>
    </div>
  );
}
