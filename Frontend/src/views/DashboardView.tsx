import React, { useEffect, useState } from 'react';
import { FileText, Clock, CheckCircle, XCircle, Plus, Search, Filter, ChevronRight, Sparkles, Building2 } from 'lucide-react';
import { contractsApi } from '../api/client';
import { ContractResponse, ContractStatus } from '../types';
import { useAuth } from '../context/AuthContext';
import StatusBadge from '../components/StatusBadge';

interface DashboardViewProps {
  onNavigate: (view: string, id?: string) => void;
}

const STATUS_FILTERS = [
  { label: 'All Contracts', value: '' },
  { label: 'Active (Approved)', value: 'APPROVED' },
  { label: 'Pending Review', value: 'PENDING_REVIEW' },
  { label: 'Rejected', value: 'REJECTED' },
  { label: 'Draft', value: 'DRAFT' },
  { label: 'Archived', value: 'ARCHIVED' },
];

export default function DashboardView({ onNavigate }: DashboardViewProps) {
  const { user } = useAuth();
  const [contracts, setContracts] = useState<ContractResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('');

  const loadContracts = async () => {
    setLoading(true);
    try {
      const { data } = await contractsApi.getAll({ search: search || undefined, status: statusFilter || undefined });
      setContracts(data);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadContracts(); }, [statusFilter]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    loadContracts();
  };

  const stats = {
    total: contracts.length,
    active: contracts.filter(c => c.status === 'APPROVED').length,
    pending: contracts.filter(c => c.status === 'PENDING_REVIEW').length,
    rejected: contracts.filter(c => c.status === 'REJECTED').length,
  };

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto space-y-6">
      {/* Header Banner */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 pb-2">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">
              Contracts <span className="gradient-text">Overview</span>
            </h1>
            <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-indigo-50 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-800">
              Live Workspace
            </span>
          </div>
          <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1">
            Welcome back, <span className="font-semibold text-slate-800 dark:text-slate-200">{user?.name}</span> • {user?.department || 'Legal Operations'}
          </p>
        </div>

        {(user?.role === 'ADMIN' || user?.role === 'EDITOR') && (
          <button
            onClick={() => onNavigate('new-contract')}
            className="flex items-center justify-center gap-2 px-4 py-2.5 text-xs font-bold rounded-lg btn-corporate-primary self-start sm:self-auto"
          >
            <Plus size={16} />
            <span>Create New Contract</span>
          </button>
        )}
      </div>

      {/* KPI Stats Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {[
          {
            label: 'Total Contracts',
            value: stats.total,
            icon: FileText,
            iconBg: 'bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400',
            textColor: 'text-indigo-600 dark:text-indigo-400',
            sub: 'In repository'
          },
          {
            label: 'Active & Approved',
            value: stats.active,
            icon: CheckCircle,
            iconBg: 'bg-emerald-50 dark:bg-emerald-950/60 text-emerald-600 dark:text-emerald-400',
            textColor: 'text-emerald-600 dark:text-emerald-400',
            sub: 'Legally binding'
          },
          {
            label: 'Pending Review',
            value: stats.pending,
            icon: Clock,
            iconBg: 'bg-amber-50 dark:bg-amber-950/60 text-amber-600 dark:text-amber-400',
            textColor: 'text-amber-600 dark:text-amber-400',
            sub: 'Awaiting reviewer action'
          },
          {
            label: 'Reverted / Rejected',
            value: stats.rejected,
            icon: XCircle,
            iconBg: 'bg-rose-50 dark:bg-rose-950/60 text-rose-600 dark:text-rose-400',
            textColor: 'text-rose-600 dark:text-rose-400',
            sub: 'Reverted to baseline'
          },
        ].map((stat) => {
          const Icon = stat.icon;
          return (
            <div
              key={stat.label}
              className="card-corporate p-5 flex items-center justify-between"
            >
              <div>
                <p className="text-xs font-semibold text-slate-500 dark:text-slate-400 mb-1">
                  {stat.label}
                </p>
                <p className={`text-3xl font-extrabold ${stat.textColor} tracking-tight leading-none`}>
                  {stat.value}
                </p>
                <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-1">
                  {stat.sub}
                </p>
              </div>
              <div className={`w-12 h-12 rounded-xl flex items-center justify-center ${stat.iconBg} shadow-sm`}>
                <Icon size={22} />
              </div>
            </div>
          );
        })}
      </div>

      {/* Filters & Search Toolbar */}
      <div className="card-corporate p-4 sm:p-5">
        <div className="flex flex-col md:flex-row gap-4 items-stretch md:items-center justify-between">
          <form onSubmit={handleSearch} className="flex gap-2 flex-1 max-w-lg">
            <div className="relative flex-1">
              <Search
                size={16}
                className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400"
              />
              <input
                type="text"
                placeholder="Search by contract title, party, or tag..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="w-full pl-10 pr-3.5 py-2 text-xs input-corporate"
              />
            </div>
            <button
              type="submit"
              className="px-4 py-2 text-xs font-bold rounded-lg btn-corporate-primary"
            >
              Search
            </button>
          </form>

          {/* Status Pills */}
          <div className="flex gap-1.5 flex-wrap items-center">
            {STATUS_FILTERS.map((f) => {
              const active = statusFilter === f.value;
              return (
                <button
                  key={f.value}
                  onClick={() => setStatusFilter(f.value)}
                  className={`px-3 py-1.5 rounded-full text-xs font-semibold transition-all ${
                    active
                      ? 'btn-corporate-primary shadow-sm'
                      : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200 hover:bg-slate-200 dark:hover:bg-slate-700'
                  }`}
                >
                  {f.label}
                </button>
              );
            })}
          </div>
        </div>
      </div>

      {/* Contracts Table Card */}
      <div className="card-corporate overflow-hidden">
        <div className="px-5 py-4 border-b border-slate-100 dark:border-slate-800 bg-slate-50/60 dark:bg-slate-800/40 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <FileText size={16} className="text-indigo-600 dark:text-indigo-400" />
            <span className="text-xs font-bold uppercase tracking-wider text-slate-800 dark:text-slate-200">
              Contract Repository ({contracts.length})
            </span>
          </div>
          <span className="text-[11px] text-slate-400 font-medium">Click row for full revision studio</span>
        </div>

        {loading ? (
          <div className="py-16 text-center text-slate-400">
            <div className="w-8 h-8 border-2 border-indigo-600 border-t-transparent rounded-full animate-spin mx-auto mb-3" />
            <p className="text-xs font-medium">Fetching contract repository...</p>
          </div>
        ) : contracts.length === 0 ? (
          <div className="py-16 text-center text-slate-400">
            <FileText size={42} className="opacity-30 mx-auto mb-3 text-indigo-400" />
            <p className="text-sm font-semibold text-slate-600 dark:text-slate-300">No contracts found matching your filters.</p>
            {(user?.role === 'ADMIN' || user?.role === 'EDITOR') && (
              <button
                onClick={() => onNavigate('new-contract')}
                className="mt-3 inline-flex items-center gap-1.5 px-3.5 py-1.5 text-xs font-bold rounded-lg btn-corporate-primary"
              >
                <Plus size={14} />
                <span>Create New Contract</span>
              </button>
            )}
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-slate-200 dark:border-slate-800 text-[11px] font-bold text-slate-400 uppercase tracking-wider bg-slate-50/30 dark:bg-slate-900/30">
                  <th className="px-5 py-3">Contract Title & Description</th>
                  <th className="px-5 py-3">Contract Type</th>
                  <th className="px-5 py-3">Active Version</th>
                  <th className="px-5 py-3">Status</th>
                  <th className="px-5 py-3 text-right">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800/60 text-xs">
                {contracts.map((contract) => (
                  <tr
                    key={contract.id}
                    onClick={() => onNavigate('contract', contract.id)}
                    className="hover:bg-indigo-50/40 dark:hover:bg-indigo-950/20 cursor-pointer transition-colors group"
                  >
                    <td className="px-5 py-3.5 max-w-sm">
                      <div className="font-bold text-slate-900 dark:text-slate-100 group-hover:text-indigo-600 dark:group-hover:text-indigo-400 transition-colors">
                        {contract.title}
                      </div>
                      <div className="text-[11px] text-slate-500 dark:text-slate-400 flex items-center gap-1.5 mt-0.5">
                        <Building2 size={12} className="text-slate-400 flex-shrink-0" />
                        <span className="truncate">{contract.parties?.join(' • ') || '—'}</span>
                      </div>
                    </td>
                    <td className="px-5 py-3.5">
                      <span className="px-2.5 py-1 rounded-md bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 font-medium text-[11px]">
                        {contract.contractType}
                      </span>
                    </td>
                    <td className="px-5 py-3.5">
                      <span className="inline-flex items-center px-2.5 py-1 rounded-md bg-indigo-50 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 font-mono font-bold text-xs border border-indigo-200 dark:border-indigo-800">
                        v{contract.currentVersionNumber}
                      </span>
                    </td>
                    <td className="px-5 py-3.5">
                      <StatusBadge status={contract.status as ContractStatus} />
                    </td>
                    <td className="px-5 py-3.5 text-right">
                      <div className="inline-flex items-center gap-1 text-xs font-semibold text-indigo-600 dark:text-indigo-400 group-hover:translate-x-0.5 transition-transform">
                        <span>Inspect</span>
                        <ChevronRight size={15} />
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
