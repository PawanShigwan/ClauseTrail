import { useEffect, useState } from 'react';
import { contractsApi } from '../api/client';
import { ContractResponse, Clause, VersionResponse, ContractStatus } from '../types';
import { useAuth } from '../context/AuthContext';
import StatusBadge from '../components/StatusBadge';
import ClauseModifyModal from './ClauseModifyModal';
import PDFExportModal from '../components/PDFExportModal';
import {
  ArrowLeft, Edit3, Download, History, GitCompare,
  FileText, Calendar, DollarSign, Building, Clock, ChevronRight
} from 'lucide-react';

interface ContractDetailViewProps {
  contractId: string;
  onNavigate: (view: string, id?: string) => void;
}

export default function ContractDetailView({ contractId, onNavigate }: ContractDetailViewProps) {
  const { user } = useAuth();
  const [contract, setContract] = useState<ContractResponse | null>(null);
  const [versions, setVersions] = useState<VersionResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedClause, setSelectedClause] = useState<Clause | undefined>(undefined);
  const [showModifyModal, setShowModifyModal] = useState(false);
  const [showPdfModal, setShowPdfModal] = useState(false);

  const canEdit = user?.role === 'ADMIN' || user?.role === 'EDITOR';
  const canReview = user?.role === 'ADMIN' || user?.role === 'REVIEWER';

  const loadData = async () => {
    setLoading(true);
    try {
      const [contractRes, versionsRes] = await Promise.all([
        contractsApi.getById(contractId, true),
        contractsApi.getVersionHistory(contractId)
      ]);
      setContract(contractRes.data);
      setVersions(versionsRes.data);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [contractId]);

  if (loading) {
    return (
      <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>
        <div style={{ width: '36px', height: '36px', border: '3px solid rgba(99,102,241,0.3)', borderTop: '3px solid #6366f1', borderRadius: '50%', animation: 'spin 1s linear infinite', margin: '0 auto 1rem' }} />
        Loading contract details...
      </div>
    );
  }

  if (!contract) {
    return (
      <div style={{ padding: '3rem', textAlign: 'center' }}>
        <h3>Contract not found</h3>
        <button onClick={() => onNavigate('dashboard')} className="btn-secondary" style={{ marginTop: '1rem' }}>
          Back to Dashboard
        </button>
      </div>
    );
  }

  const activeClauses: Clause[] = contract.activeVersion?.clauses || [];

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto space-y-6">
      {/* Back Button */}
      <button
        onClick={() => onNavigate('dashboard')}
        className="inline-flex items-center gap-1.5 text-xs font-bold text-slate-500 hover:text-indigo-600 dark:text-slate-400 dark:hover:text-indigo-300 transition-colors group"
      >
        <ArrowLeft size={15} className="group-hover:-translate-x-0.5 transition-transform" />
        <span>Back to Contracts</span>
      </button>

      {/* Pending Review Banner */}
      {contract.status === 'PENDING_REVIEW' && (
        <div className="card-corporate p-5 border-l-4 border-l-amber-500 bg-gradient-to-r from-amber-50/80 via-white to-amber-50/30 dark:from-amber-950/40 dark:via-slate-900 dark:to-slate-900 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div className="flex items-start gap-3">
            <div className="w-10 h-10 rounded-xl bg-amber-100 dark:bg-amber-950/80 text-amber-700 dark:text-amber-400 flex items-center justify-center flex-shrink-0 shadow-sm">
              <Clock size={20} className="animate-pulse" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h3 className="text-sm font-bold text-slate-900 dark:text-white">
                  Pending Review — Revision v{contract.pendingVersionNumber} Proposed
                </h3>
                <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-100 dark:bg-amber-900/60 text-amber-800 dark:text-amber-200">
                  Action Needed
                </span>
              </div>
              <p className="text-xs text-slate-600 dark:text-slate-400 mt-0.5">
                Submitted by <span className="font-semibold text-slate-800 dark:text-slate-200">{contract.pendingVersion?.modifiedBy?.name || 'an editor'}</span> — "{contract.pendingVersion?.modificationReason || 'No justification provided'}"
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2.5 self-start sm:self-auto">
            <button
              onClick={() => onNavigate('diff', `${contract.id}?v1=${contract.currentVersionNumber}&v2=${contract.pendingVersionNumber}`)}
              className="flex items-center gap-1.5 px-3.5 py-2 text-xs font-bold rounded-lg btn-corporate-secondary"
            >
              <GitCompare size={14} className="text-indigo-600 dark:text-indigo-400" />
              <span>Inspect Diff</span>
            </button>
            {canReview && (
              <button
                onClick={() => onNavigate('review-queue')}
                className="flex items-center gap-1.5 px-4 py-2 text-xs font-bold rounded-lg btn-corporate-primary"
              >
                <span>Review Queue</span>
                <ChevronRight size={14} />
              </button>
            )}
          </div>
        </div>
      )}

      {/* Contract Header Card */}
      <div className="card-corporate p-6 sm:p-7">
        <div className="flex flex-col lg:flex-row lg:items-start lg:justify-between gap-5">
          <div className="space-y-2 max-w-3xl">
            <div className="flex flex-wrap items-center gap-2.5">
              <h1 className="text-xl sm:text-2xl font-extrabold text-slate-900 dark:text-white tracking-tight">
                {contract.title}
              </h1>
              <StatusBadge status={contract.status as ContractStatus} />
              <span className="px-2.5 py-1 rounded-md text-xs font-mono font-bold bg-indigo-50 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-800">
                v{contract.currentVersionNumber} Active
              </span>
            </div>
            <p className="text-xs sm:text-sm text-slate-600 dark:text-slate-400 leading-relaxed">
              {contract.description || 'Enterprise agreement stored in immutable ClauseTrail ledger.'}
            </p>
          </div>

          {/* Action Buttons */}
          <div className="flex items-center gap-2.5 flex-wrap">
            <button
              onClick={() => onNavigate('timeline', contract.id)}
              className="flex items-center gap-1.5 px-3.5 py-2 text-xs font-bold rounded-lg btn-corporate-secondary"
            >
              <History size={14} />
              <span>Version History</span>
            </button>
            <button
              onClick={() => setShowPdfModal(true)}
              className="flex items-center gap-1.5 px-3.5 py-2 text-xs font-bold rounded-lg btn-corporate-secondary"
            >
              <Download size={14} />
              <span>Export PDF</span>
            </button>
            {canEdit && contract.status !== 'PENDING_REVIEW' && (
              <button
                onClick={() => {
                  setSelectedClause(activeClauses[0]);
                  setShowModifyModal(true);
                }}
                className="flex items-center gap-1.5 px-4 py-2 text-xs font-bold rounded-lg btn-corporate-primary"
              >
                <Edit3 size={14} />
                <span>Propose Revision</span>
              </button>
            )}
          </div>
        </div>

        {/* Metadata Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mt-6 pt-6 border-t border-slate-100 dark:border-slate-800">
          <div className="p-3.5 rounded-xl bg-slate-50/70 dark:bg-slate-800/40 border border-slate-200/60 dark:border-slate-800">
            <div className="text-[11px] font-bold text-slate-400 uppercase tracking-wider mb-1 flex items-center gap-1.5">
              <Building size={13} className="text-indigo-600 dark:text-indigo-400" />
              <span>Contract Parties</span>
            </div>
            <div className="text-xs font-bold text-slate-900 dark:text-slate-100 truncate">
              {contract.parties?.join(' & ') || '—'}
            </div>
          </div>

          <div className="p-3.5 rounded-xl bg-slate-50/70 dark:bg-slate-800/40 border border-slate-200/60 dark:border-slate-800">
            <div className="text-[11px] font-bold text-slate-400 uppercase tracking-wider mb-1 flex items-center gap-1.5">
              <FileText size={13} className="text-indigo-600 dark:text-indigo-400" />
              <span>Category / Type</span>
            </div>
            <div className="text-xs font-bold text-slate-900 dark:text-slate-100 truncate">
              {contract.contractType || 'Master Agreement'}
            </div>
          </div>

          <div className="p-3.5 rounded-xl bg-slate-50/70 dark:bg-slate-800/40 border border-slate-200/60 dark:border-slate-800">
            <div className="text-[11px] font-bold text-slate-400 uppercase tracking-wider mb-1 flex items-center gap-1.5">
              <Calendar size={13} className="text-indigo-600 dark:text-indigo-400" />
              <span>Effective Term</span>
            </div>
            <div className="text-xs font-bold text-slate-900 dark:text-slate-100 truncate">
              {contract.effectiveDate || '2026-08-01'} → {contract.expirationDate || 'Ongoing'}
            </div>
          </div>

          <div className="p-3.5 rounded-xl bg-slate-50/70 dark:bg-slate-800/40 border border-slate-200/60 dark:border-slate-800">
            <div className="text-[11px] font-bold text-slate-400 uppercase tracking-wider mb-1 flex items-center gap-1.5">
              <DollarSign size={13} className="text-emerald-600 dark:text-emerald-400" />
              <span>Contract Value</span>
            </div>
            <div className="text-xs font-extrabold text-emerald-600 dark:text-emerald-400 truncate">
              {contract.contractValue || 'Standard Commercial'}
            </div>
          </div>
        </div>
      </div>

      {/* Clauses Section */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <h2 className="text-base font-extrabold text-slate-900 dark:text-white">
              Approved Contract Clauses ({activeClauses.length})
            </h2>
            <span className="text-[11px] text-slate-500 dark:text-slate-400">
              • Active v{contract.currentVersionNumber}
            </span>
          </div>
        </div>

        <div className="space-y-3.5">
          {activeClauses.map((clause: Clause, idx: number) => (
            <div
              key={clause.id || idx}
              className="card-corporate p-5 transition-all"
            >
              <div className="flex items-center justify-between gap-3 mb-2.5">
                <div className="flex items-center gap-2.5 flex-wrap">
                  <span className="font-mono text-xs font-bold px-2 py-0.5 rounded bg-indigo-50 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-800">
                    Clause {clause.clauseNumber || idx + 1}
                  </span>
                  <h3 className="text-sm font-bold text-slate-900 dark:text-white">
                    {clause.title}
                  </h3>
                  {clause.category && (
                    <span className="text-[10px] font-semibold text-slate-500 dark:text-slate-400 bg-slate-100 dark:bg-slate-800 px-2 py-0.5 rounded-full border border-slate-200 dark:border-slate-700">
                      {clause.category}
                    </span>
                  )}
                </div>

                {canEdit && contract.status !== 'PENDING_REVIEW' && (
                  <button
                    onClick={() => {
                      setSelectedClause(clause);
                      setShowModifyModal(true);
                    }}
                    className="flex items-center gap-1 px-2.5 py-1 text-xs font-semibold rounded-md text-indigo-700 dark:text-indigo-300 bg-indigo-50 dark:bg-indigo-950/60 hover:bg-indigo-100 dark:hover:bg-indigo-900/60 border border-indigo-200 dark:border-indigo-800 transition-colors"
                  >
                    <Edit3 size={12} />
                    <span>Edit Clause</span>
                  </button>
                )}
              </div>

              <p className="text-xs sm:text-sm text-slate-700 dark:text-slate-300 leading-relaxed whitespace-pre-wrap">
                {clause.content}
              </p>
            </div>
          ))}
        </div>
      </div>

      {/* Modals */}
      {showModifyModal && (
        <ClauseModifyModal
          contractId={contract.id}
          contractTitle={contract.title}
          clauses={activeClauses}
          selectedClause={selectedClause}
          onClose={() => setShowModifyModal(false)}
          onSuccess={() => {
            setShowModifyModal(false);
            loadData();
          }}
        />
      )}

      {showPdfModal && (
        <PDFExportModal
          contractId={contract.id}
          contractTitle={contract.title}
          versions={versions}
          currentVersionNumber={contract.currentVersionNumber}
          onClose={() => setShowPdfModal(false)}
        />
      )}
    </div>
  );
}
