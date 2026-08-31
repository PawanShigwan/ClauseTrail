import { useEffect, useState } from 'react';
import { reviewApi, contractsApi } from '../api/client';
import { ReviewQueueItemDTO, DiffResponse, VersionStatus } from '../types';
import DiffVisualizer from '../components/DiffVisualizer';
import confetti from 'canvas-confetti';
import {
  CheckCircle, XCircle, GitCompare,
  User, Calendar, X
} from 'lucide-react';

interface ReviewQueueViewProps {
  onNavigate?: (view: string, id?: string) => void;
}

export default function ReviewQueueView({ onNavigate }: ReviewQueueViewProps) {
  const [queue, setQueue] = useState<ReviewQueueItemDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedItem, setSelectedItem] = useState<ReviewQueueItemDTO | null>(null);
  const [diffData, setDiffData] = useState<DiffResponse | null>(null);
  const [diffLoading, setDiffLoading] = useState(false);
  const [comments, setComments] = useState('');
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState('');

  const loadQueue = async () => {
    setLoading(true);
    try {
      const { data } = await reviewApi.getReviewQueue();
      setQueue(data);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadQueue();
  }, []);

  const openReviewModal = async (item: ReviewQueueItemDTO) => {
    setSelectedItem(item);
    setComments('');
    setError('');
    setDiffLoading(true);
    try {
      const { data } = await contractsApi.getDiff(item.contractId, item.parentVersionNumber, item.versionNumber);
      setDiffData(data);
    } catch (e) {
      console.error('Failed to load diff', e);
    } finally {
      setDiffLoading(false);
    }
  };

  const handleReviewAction = async (action: 'APPROVED' | 'REJECTED') => {
    if (!selectedItem) return;
    setActionLoading(true);
    setError('');
    try {
      await reviewApi.reviewContract(selectedItem.contractId, {
        action: action as VersionStatus,
        reviewComments: comments
      });

      if (action === 'APPROVED') {
        confetti({
          particleCount: 80,
          spread: 70,
          origin: { y: 0.6 }
        });
      }

      setSelectedItem(null);
      loadQueue();
    } catch (err: any) {
      setError(err?.response?.data?.message || `Failed to ${action.toLowerCase()} modification.`);
    } finally {
      setActionLoading(false);
    }
  };

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto space-y-6">
      {/* Header */}
      <div className="space-y-1">
        <div className="flex items-center gap-2.5">
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">
            Review <span className="gradient-text">Queue</span>
          </h1>
          <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-amber-50 dark:bg-amber-950/60 text-amber-800 dark:text-amber-300 border border-amber-200 dark:border-amber-800">
            {queue.length} Pending
          </span>
        </div>
        <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400">
          Inspect proposed contract modifications, evaluate word diffs, and approve or reject submissions.
        </p>
      </div>

      {loading ? (
        <div className="py-16 text-center text-slate-400">
          <div className="w-8 h-8 border-2 border-indigo-600 border-t-transparent rounded-full animate-spin mx-auto mb-3" />
          <p className="text-xs font-medium">Loading review queue...</p>
        </div>
      ) : queue.length === 0 ? (
        <div className="card-corporate p-12 text-center max-w-lg mx-auto">
          <div className="w-16 h-16 rounded-2xl bg-emerald-50 dark:bg-emerald-950/60 text-emerald-600 dark:text-emerald-400 flex items-center justify-center mx-auto mb-4 shadow-sm">
            <CheckCircle size={32} />
          </div>
          <h3 className="text-lg font-bold text-slate-900 dark:text-white mb-1">Queue is All Clear!</h3>
          <p className="text-xs text-slate-500 dark:text-slate-400 leading-relaxed">
            All proposed contract revisions have been reviewed. New editor modifications will appear here in real-time.
          </p>
        </div>
      ) : (
        <div className="space-y-4">
          {queue.map((item) => (
            <div
              key={item.versionId}
              className="card-corporate p-6 space-y-4 text-left"
            >
              <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
                <div className="space-y-2">
                  <div className="flex items-center gap-2 flex-wrap">
                    <span className="font-mono text-xs font-bold px-2.5 py-0.5 rounded-md bg-amber-50 dark:bg-amber-950/60 text-amber-800 dark:text-amber-300 border border-amber-200 dark:border-amber-800">
                      Revision v{item.versionNumber} Proposed
                    </span>
                    <span className="text-xs text-slate-400">
                      (from baseline v{item.parentVersionNumber})
                    </span>
                    <span className="text-xs font-semibold px-2 py-0.5 rounded-full bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 border border-slate-200 dark:border-slate-700">
                      {item.contractType}
                    </span>
                  </div>

                  <h3 className="text-lg font-bold text-slate-900 dark:text-white">
                    {item.contractTitle}
                  </h3>

                  <div className="flex items-center gap-4 flex-wrap text-xs text-slate-500 dark:text-slate-400">
                    <span className="flex items-center gap-1.5">
                      <User size={13} className="text-indigo-600 dark:text-indigo-400" />
                      <span>Author: <strong className="text-slate-800 dark:text-slate-200">{item.modifiedBy?.name}</strong> ({item.modifiedBy?.role})</span>
                    </span>
                    <span className="flex items-center gap-1.5">
                      <Calendar size={13} className="text-indigo-600 dark:text-indigo-400" />
                      <span>{new Date(item.submittedAt).toLocaleString()}</span>
                    </span>
                    <span className="font-bold text-indigo-600 dark:text-indigo-400">
                      {item.clausesChangedCount} {item.clausesChangedCount === 1 ? 'clause' : 'clauses'} modified
                    </span>
                  </div>

                  {/* Modification reason callout */}
                  <div className="p-3.5 rounded-xl bg-amber-50/60 dark:bg-amber-950/30 border border-amber-200/80 dark:border-amber-800/80 text-xs">
                    <span className="font-bold text-amber-900 dark:text-amber-300">Mandatory Justification: </span>
                    <span className="text-slate-700 dark:text-slate-300">"{item.modificationReason}"</span>
                  </div>
                </div>

                <div className="flex-shrink-0 self-start lg:self-center">
                  <button
                    onClick={() => openReviewModal(item)}
                    className="flex items-center gap-2 px-5 py-2.5 text-xs font-bold rounded-lg btn-corporate-primary"
                  >
                    <GitCompare size={15} />
                    <span>Inspect & Review Diff</span>
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Review Modal with Diff Visualizer */}
      {selectedItem && (
        <div className="fixed inset-0 bg-slate-900/70 backdrop-blur-sm flex items-center justify-center z-50 p-4 sm:p-6 animate-fade-in">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-corporate-hover w-full max-w-5xl max-h-[92vh] flex flex-col overflow-hidden text-left">
            {/* Modal Header */}
            <div className="px-6 py-4 border-b border-slate-100 dark:border-slate-800 bg-slate-50/70 dark:bg-slate-800/40 flex items-center justify-between">
              <div>
                <div className="flex items-center gap-2">
                  <h3 className="text-base font-bold text-slate-900 dark:text-white">{selectedItem.contractTitle}</h3>
                  <span className="px-2 py-0.5 rounded-md bg-amber-50 dark:bg-amber-950/60 text-amber-800 dark:text-amber-300 text-xs font-bold border border-amber-200 dark:border-amber-800">
                    Reviewing v{selectedItem.versionNumber}
                  </span>
                </div>
                <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
                  Proposed by {selectedItem.modifiedBy?.name} — Reason: "{selectedItem.modificationReason}"
                </p>
              </div>
              <button
                onClick={() => setSelectedItem(null)}
                className="p-1.5 rounded-lg text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
              >
                <X size={20} />
              </button>
            </div>

            {/* Modal Diff Body */}
            <div className="p-6 overflow-y-auto space-y-5 flex-1">
              {error && (
                <div className="p-3.5 rounded-lg bg-rose-50 dark:bg-rose-950/50 border border-rose-200 dark:border-rose-800 text-rose-700 dark:text-rose-300 text-xs font-semibold">
                  {error}
                </div>
              )}

              {diffLoading ? (
                <div className="py-16 text-center text-slate-400">
                  <div className="w-8 h-8 border-2 border-indigo-600 border-t-transparent rounded-full animate-spin mx-auto mb-3" />
                  <p className="text-xs font-medium">Computing word-level diff...</p>
                </div>
              ) : diffData ? (
                <DiffVisualizer diff={diffData} />
              ) : (
                <div className="text-center text-slate-400 py-8 text-xs">Failed to load diff preview.</div>
              )}

              {/* Review Comments Box */}
              <div className="pt-2">
                <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5">
                  Review Notes / Audit Comments:
                </label>
                <textarea
                  value={comments}
                  onChange={(e) => setComments(e.target.value)}
                  rows={3}
                  placeholder="Optional approval notes or reason for rejection (logged to immutable audit trail)..."
                  className="w-full p-3.5 text-xs input-corporate leading-relaxed"
                />
              </div>
            </div>

            {/* Modal Footer Actions */}
            <div className="px-6 py-4 border-t border-slate-100 dark:border-slate-800 bg-slate-50/60 dark:bg-slate-800/40 flex items-center justify-between">
              <button
                onClick={() => setSelectedItem(null)}
                className="px-4 py-2 text-xs font-semibold rounded-lg btn-corporate-secondary"
              >
                Cancel
              </button>

              <div className="flex items-center gap-3">
                <button
                  onClick={() => handleReviewAction('REJECTED')}
                  disabled={actionLoading}
                  className="flex items-center gap-1.5 px-4 py-2 text-xs font-bold rounded-lg bg-rose-50 dark:bg-rose-950/50 text-rose-700 dark:text-rose-300 hover:bg-rose-100 dark:hover:bg-rose-900/60 border border-rose-200 dark:border-rose-800 transition-colors disabled:opacity-50"
                >
                  <XCircle size={15} />
                  <span>Reject Changes</span>
                </button>
                <button
                  onClick={() => handleReviewAction('APPROVED')}
                  disabled={actionLoading}
                  className="flex items-center gap-1.5 px-5 py-2 text-xs font-bold rounded-lg bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 text-white shadow-corporate-btn transition-all disabled:opacity-50"
                >
                  <CheckCircle size={15} />
                  <span>Approve & Promote (v{selectedItem.versionNumber})</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
