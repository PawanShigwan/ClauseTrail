import { useEffect, useState } from 'react';
import { contractsApi } from '../api/client';
import { ContractResponse, VersionResponse } from '../types';
import StatusBadge from '../components/StatusBadge';
import {
  ArrowLeft, History, User, Calendar,
  CheckCircle, GitCompare
} from 'lucide-react';

interface VersionHistoryTimelineViewProps {
  contractId: string;
  onNavigate: (view: string, id?: string) => void;
}

export default function VersionHistoryTimelineView({ contractId, onNavigate }: VersionHistoryTimelineViewProps) {
  const [contract, setContract] = useState<ContractResponse | null>(null);
  const [versions, setVersions] = useState<VersionResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const load = async () => {
      setLoading(true);
      try {
        const [contractRes, versionsRes] = await Promise.all([
          contractsApi.getById(contractId),
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
    load();
  }, [contractId]);

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-5xl mx-auto space-y-6">
      {/* Back button */}
      <button
        onClick={() => onNavigate('contract', contractId)}
        className="inline-flex items-center gap-1.5 text-xs font-bold text-slate-500 hover:text-indigo-600 dark:text-slate-400 dark:hover:text-indigo-300 transition-colors group"
      >
        <ArrowLeft size={15} className="group-hover:-translate-x-0.5 transition-transform" />
        <span>Back to Contract</span>
      </button>

      {/* Header Card */}
      <div className="card-corporate p-6">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-indigo-600 to-violet-600 text-white flex items-center justify-center shadow-corporate-btn">
            <History size={20} />
          </div>
          <div>
            <h1 className="text-xl sm:text-2xl font-extrabold text-slate-900 dark:text-white tracking-tight">
              Version History <span className="gradient-text">Timeline</span>
            </h1>
            <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-0.5">
              {contract?.title || 'Contract'} — Immutable revision log & cryptographic chain
            </p>
          </div>
        </div>
      </div>

      {loading ? (
        <div className="py-20 text-center text-slate-400">
          <div className="w-8 h-8 border-2 border-indigo-600 border-t-transparent rounded-full animate-spin mx-auto mb-3" />
          <p className="text-xs font-medium">Loading version timeline...</p>
        </div>
      ) : (
        <div className="relative pl-7 sm:pl-9 space-y-6 text-left">
          {/* Vertical Spine Line */}
          <div className="absolute left-[13px] sm:left-[17px] top-4 bottom-4 w-0.5 bg-gradient-to-b from-indigo-600 via-violet-500 to-emerald-500 rounded-full" />

          {versions.map((ver) => {
            const isCurrent = ver.versionNumber === contract?.currentVersionNumber;
            const isApproved = ver.status === 'APPROVED';
            const isPending = ver.status === 'PENDING_REVIEW';

            const dotBg = isApproved
              ? 'bg-emerald-500 ring-4 ring-emerald-100 dark:ring-emerald-950'
              : isPending
              ? 'bg-amber-500 ring-4 ring-amber-100 dark:ring-amber-950 animate-pulse'
              : 'bg-rose-500 ring-4 ring-rose-100 dark:ring-rose-950';

            return (
              <div key={ver.id} className="relative">
                {/* Node Dot */}
                <div className={`absolute -left-[27px] sm:-left-[31px] top-6 w-3.5 h-3.5 rounded-full ${dotBg} z-10`} />

                <div className={`card-corporate p-5 sm:p-6 transition-all ${
                  isCurrent ? 'ring-2 ring-indigo-500/40 bg-indigo-50/20 dark:bg-indigo-950/20' : ''
                }`}>
                  <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-3">
                    <div className="flex items-center gap-2.5 flex-wrap">
                      <span className="font-mono text-xs font-bold px-2.5 py-1 rounded-md bg-indigo-50 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-800">
                        Version {ver.versionNumber}
                      </span>

                      {isCurrent && (
                        <span className="px-2 py-0.5 rounded-full bg-emerald-50 dark:bg-emerald-950/60 text-emerald-700 dark:text-emerald-300 text-[10px] font-bold border border-emerald-200 dark:border-emerald-800">
                          ACTIVE BASELINE
                        </span>
                      )}

                      <StatusBadge status={ver.status as any} />
                    </div>

                    {/* Compare Button */}
                    {ver.parentVersionNumber && (
                      <button
                        onClick={() => onNavigate('diff', `${contractId}?v1=${ver.parentVersionNumber}&v2=${ver.versionNumber}`)}
                        className="inline-flex items-center gap-1 px-3 py-1.5 text-xs font-semibold rounded-lg btn-corporate-secondary self-start sm:self-auto"
                      >
                        <GitCompare size={13} className="text-indigo-600 dark:text-indigo-400" />
                        <span>Compare with v{ver.parentVersionNumber}</span>
                      </button>
                    )}
                  </div>

                  {/* Modification Note */}
                  <div className="p-3.5 rounded-xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200 dark:border-slate-700 mb-4">
                    <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-1">
                      Modification Reason / Justification
                    </div>
                    <div className="text-xs sm:text-sm text-slate-800 dark:text-slate-200 leading-relaxed">
                      {ver.modificationReason || 'Initial baseline contract agy-seeded'}
                    </div>
                  </div>

                  {/* Metadata Footer */}
                  <div className="flex items-center gap-4 flex-wrap text-xs text-slate-500 dark:text-slate-400 pt-3 border-t border-slate-100 dark:border-slate-800">
                    <span className="flex items-center gap-1.5">
                      <User size={13} className="text-indigo-600 dark:text-indigo-400" />
                      <span>Author: <strong className="text-slate-800 dark:text-slate-200">{ver.modifiedBy?.name || 'System'}</strong></span>
                    </span>
                    <span className="flex items-center gap-1.5">
                      <Calendar size={13} className="text-indigo-600 dark:text-indigo-400" />
                      <span>{new Date(ver.createdAt).toLocaleString()}</span>
                    </span>
                    {ver.reviewedBy && (
                      <span className="flex items-center gap-1.5 text-emerald-600 dark:text-emerald-400 font-semibold">
                        <CheckCircle size={13} />
                        <span>Reviewed by {ver.reviewedBy.name}</span>
                      </span>
                    )}
                    <span className="ml-auto font-mono text-[11px] text-slate-400">
                      {ver.clauses?.length || 0} clauses
                    </span>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
