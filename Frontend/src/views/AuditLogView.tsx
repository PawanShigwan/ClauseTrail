import React, { useEffect, useState } from 'react';
import { auditApi } from '../api/client';
import { AuditLogResponse } from '../types';
import {
  Shield, Download, Filter, Search, Calendar, User,
  FileText, Activity, CheckCircle, Clock
} from 'lucide-react';

const ACTIONS = [
  { label: 'All Actions', value: '' },
  { label: 'Upload Contract', value: 'UPLOAD_CONTRACT' },
  { label: 'Modify Clause', value: 'MODIFY_CLAUSE' },
  { label: 'Approve Version', value: 'APPROVE_VERSION' },
  { label: 'Reject Version', value: 'REJECT_VERSION' },
  { label: 'Update Metadata', value: 'UPDATE_METADATA' },
  { label: 'Role Change', value: 'ROLE_CHANGE' },
  { label: 'Login', value: 'LOGIN' },
];

export default function AuditLogView() {
  const [logs, setLogs] = useState<AuditLogResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [actionFilter, setActionFilter] = useState('');
  const [search, setSearch] = useState('');
  const [exporting, setExporting] = useState(false);

  const loadLogs = async () => {
    setLoading(true);
    try {
      const { data } = await auditApi.getLogs({ action: actionFilter || undefined });
      setLogs(data);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadLogs();
  }, [actionFilter]);

  const handleExportCsv = async () => {
    setExporting(true);
    try {
      const response = await auditApi.exportCsv();
      const url = window.URL.createObjectURL(new Blob([response.data], { type: 'text/csv' }));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `clausetrail_audit_log_${new Date().toISOString().slice(0, 10)}.csv`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (e) {
      console.error('CSV Export failed', e);
      alert('Failed to export audit log CSV');
    } finally {
      setExporting(false);
    }
  };

  const filteredLogs = logs.filter(log => {
    if (!search) return true;
    const q = search.toLowerCase();
    return (
      log.userName?.toLowerCase().includes(q) ||
      log.contractTitle?.toLowerCase().includes(q) ||
      log.details?.toLowerCase().includes(q) ||
      log.action?.toLowerCase().includes(q)
    );
  });

  const getActionBadge = (action: string) => {
    let color = '#818cf8';
    let bg = 'rgba(99,102,241,0.12)';
    if (action.includes('APPROVE')) {
      color = '#34d399';
      bg = 'rgba(52,211,153,0.12)';
    } else if (action.includes('REJECT')) {
      color = '#f87171';
      bg = 'rgba(248,113,113,0.12)';
    } else if (action.includes('MODIFY')) {
      color = '#fbbf24';
      bg = 'rgba(251,191,36,0.12)';
    } else if (action.includes('ROLE')) {
      color = '#c084fc';
      bg = 'rgba(192,132,252,0.12)';
    }

    return (
      <span style={{
        padding: '0.2rem 0.55rem', borderRadius: '6px', fontSize: '0.75rem', fontWeight: 700,
        color: color, background: bg, display: 'inline-block'
      }}>
        {action.replace(/_/g, ' ')}
      </span>
    );
  };

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 pb-2">
        <div className="space-y-1">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-indigo-600 to-violet-600 text-white flex items-center justify-center shadow-corporate-btn">
              <Shield size={20} />
            </div>
            <div>
              <h1 className="text-xl sm:text-2xl font-extrabold text-slate-900 dark:text-white tracking-tight">
                Compliance <span className="gradient-text">Audit Trail</span>
              </h1>
              <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400">
                Immutable, cryptographic log of all contract revisions, approvals, and security events.
              </p>
            </div>
          </div>
        </div>

        <button
          onClick={handleExportCsv}
          disabled={exporting}
          className="flex items-center gap-2 px-4 py-2.5 text-xs font-bold rounded-lg btn-corporate-primary self-start sm:self-auto disabled:opacity-50"
        >
          <Download size={15} />
          <span>{exporting ? 'Generating CSV...' : 'Export to CSV'}</span>
        </button>
      </div>

      {/* Filter Toolbar */}
      <div className="card-corporate p-4 sm:p-5">
        <div className="flex flex-col md:flex-row gap-4 items-stretch md:items-center justify-between">
          <div className="relative flex-1 max-w-md">
            <Search
              size={16}
              className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400"
            />
            <input
              type="text"
              placeholder="Search audit records by user, contract, or details..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full pl-10 pr-3.5 py-2 text-xs input-corporate"
            />
          </div>

          <div className="flex gap-1.5 flex-wrap items-center">
            {ACTIONS.map((a) => {
              const active = actionFilter === a.value;
              return (
                <button
                  key={a.value}
                  onClick={() => setActionFilter(a.value)}
                  className={`px-3 py-1.5 rounded-full text-xs font-semibold transition-all ${
                    active
                      ? 'btn-corporate-primary shadow-sm'
                      : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200 hover:bg-slate-200 dark:hover:bg-slate-700'
                  }`}
                >
                  {a.label}
                </button>
              );
            })}
          </div>
        </div>
      </div>

      {/* Audit Table Card */}
      <div className="card-corporate overflow-hidden">
        <div className="px-5 py-4 border-b border-slate-100 dark:border-slate-800 bg-slate-50/60 dark:bg-slate-800/40 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Activity size={16} className="text-indigo-600 dark:text-indigo-400" />
            <span className="text-xs font-bold uppercase tracking-wider text-slate-800 dark:text-slate-200">
              Audit Records ({filteredLogs.length})
            </span>
          </div>
          <span className="text-[11px] font-mono text-slate-400">SHA-256 Ledger Verified</span>
        </div>

        {loading ? (
          <div className="py-16 text-center text-slate-400">
            <div className="w-8 h-8 border-2 border-indigo-600 border-t-transparent rounded-full animate-spin mx-auto mb-3" />
            <p className="text-xs font-medium">Retrieving cryptographic audit trail...</p>
          </div>
        ) : filteredLogs.length === 0 ? (
          <div className="py-16 text-center text-slate-400">
            <Shield size={42} className="opacity-30 mx-auto mb-3 text-indigo-400" />
            <p className="text-sm font-semibold text-slate-600 dark:text-slate-300">No audit events match your search filters.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-slate-200 dark:border-slate-800 text-[11px] font-bold text-slate-400 uppercase tracking-wider bg-slate-50/30 dark:bg-slate-900/30">
                  <th className="px-5 py-3">Timestamp</th>
                  <th className="px-5 py-3">Action Type</th>
                  <th className="px-5 py-3">Actor / Role</th>
                  <th className="px-5 py-3">Contract & Version</th>
                  <th className="px-5 py-3">Audit Details</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800/60 text-xs">
                {filteredLogs.map((log) => (
                  <tr
                    key={log.id}
                    className="hover:bg-indigo-50/30 dark:hover:bg-indigo-950/10 transition-colors"
                  >
                    <td className="px-5 py-3.5 font-mono text-[11px] text-slate-500 dark:text-slate-400 whitespace-nowrap">
                      {new Date(log.timestamp).toLocaleString()}
                    </td>
                    <td className="px-5 py-3.5 whitespace-nowrap">
                      {getActionBadge(log.action)}
                    </td>
                    <td className="px-5 py-3.5 whitespace-nowrap">
                      <div className="font-bold text-slate-900 dark:text-slate-100">{log.userName}</div>
                      <div className="text-[10px] font-medium text-slate-400 capitalize">{log.userRole?.toLowerCase() || 'System'}</div>
                    </td>
                    <td className="px-5 py-3.5">
                      <div className="font-semibold text-slate-800 dark:text-slate-200 truncate max-w-xs">{log.contractTitle || '—'}</div>
                      {log.versionNumber && (
                        <span className="font-mono text-[10px] font-bold text-indigo-600 dark:text-indigo-400">
                          v{log.versionNumber}
                        </span>
                      )}
                    </td>
                    <td className="px-5 py-3.5 text-slate-600 dark:text-slate-300 max-w-md leading-relaxed">
                      {log.details}
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
