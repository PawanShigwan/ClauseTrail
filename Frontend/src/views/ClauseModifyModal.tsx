import React, { useState } from 'react';
import { X, Edit3, Send, AlertTriangle } from 'lucide-react';
import { Clause, ClauseChangeDTO, ChangeType } from '../types';
import { contractsApi } from '../api/client';

interface ClauseModifyModalProps {
  contractId: string;
  contractTitle: string;
  clauses: Clause[];
  selectedClause?: Clause;
  onClose: () => void;
  onSuccess: () => void;
}

export default function ClauseModifyModal({ contractId, contractTitle, clauses, selectedClause, onClose, onSuccess }: ClauseModifyModalProps) {
  const [activeClauseId, setActiveClauseId] = useState<string>(selectedClause?.id || clauses[0]?.id || '');
  const [clauseTitle, setClauseTitle] = useState<string>(selectedClause?.title || clauses[0]?.title || '');
  const [clauseContent, setClauseContent] = useState<string>(selectedClause?.content || clauses[0]?.content || '');
  const [reason, setReason] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>('');

  const currentOriginalClause = clauses.find(c => c.id === activeClauseId) || clauses[0];

  const handleSelectClause = (id: string) => {
    const cl = clauses.find(c => c.id === id);
    if (cl) {
      setActiveClauseId(cl.id);
      setClauseTitle(cl.title);
      setClauseContent(cl.content);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!reason.trim()) {
      setError('Please provide a mandatory reason for this modification.');
      return;
    }

    if (clauseContent.trim() === currentOriginalClause.content.trim() && clauseTitle.trim() === currentOriginalClause.title.trim()) {
      setError('No changes detected in clause content or title.');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const clauseChanges: ClauseChangeDTO[] = [
        {
          clauseId: currentOriginalClause.id,
          clauseNumber: currentOriginalClause.clauseNumber,
          clauseTitle: clauseTitle,
          previousText: currentOriginalClause.content,
          modifiedText: clauseContent,
          changeType: 'MODIFIED' as ChangeType,
          reason: reason
        }
      ];

      await contractsApi.modify(contractId, {
        modificationReason: reason,
        clauseChanges: clauseChanges
      });

      onSuccess();
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Failed to submit modification.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center z-50 p-4 sm:p-6 animate-fade-in">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-corporate-hover w-full max-w-3xl max-h-[90vh] flex flex-col overflow-hidden">
        {/* Modal Header */}
        <div className="px-6 py-4 border-b border-slate-100 dark:border-slate-800 flex items-center justify-between bg-slate-50/70 dark:bg-slate-800/40">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-indigo-600 to-violet-600 flex items-center justify-center text-white shadow-corporate-btn">
              <Edit3 size={18} />
            </div>
            <div>
              <h3 className="text-base font-bold text-slate-900 dark:text-white">Propose Clause Modification</h3>
              <p className="text-xs text-slate-500 dark:text-slate-400 truncate max-w-md">{contractTitle}</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
          >
            <X size={18} />
          </button>
        </div>

        {/* Form Body */}
        <form onSubmit={handleSubmit} className="p-6 overflow-y-auto space-y-4 flex-1 text-left">
          {error && (
            <div className="p-3 rounded-lg bg-rose-50 dark:bg-rose-950/50 border border-rose-200 dark:border-rose-800 text-rose-700 dark:text-rose-300 text-xs font-semibold flex items-center gap-2">
              <AlertTriangle size={15} className="flex-shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {/* Clause Selector */}
          <div>
            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
              Select Clause to Edit:
            </label>
            <select
              value={activeClauseId}
              onChange={(e) => handleSelectClause(e.target.value)}
              className="w-full px-3.5 py-2 text-xs input-corporate font-semibold"
            >
              {clauses.map((c) => (
                <option key={c.id} value={c.id}>
                  Clause {c.clauseNumber}: {c.title}
                </option>
              ))}
            </select>
          </div>

          {/* Clause Title */}
          <div>
            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
              Clause Title:
            </label>
            <input
              type="text"
              value={clauseTitle}
              onChange={(e) => setClauseTitle(e.target.value)}
              required
              className="w-full px-3.5 py-2 text-xs input-corporate font-semibold"
            />
          </div>

          {/* Side by side original vs editor */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-bold text-slate-500 dark:text-slate-400 mb-1">
                Current Approved Baseline Text:
              </label>
              <div className="p-3.5 rounded-xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200 dark:border-slate-700 text-xs leading-relaxed text-slate-600 dark:text-slate-300 h-44 overflow-y-auto whitespace-pre-wrap">
                {currentOriginalClause?.content}
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-indigo-600 dark:text-indigo-400 mb-1">
                Proposed Revision Text:
              </label>
              <textarea
                value={clauseContent}
                onChange={(e) => setClauseContent(e.target.value)}
                required
                className="w-full p-3.5 text-xs input-corporate h-44 resize-y leading-relaxed font-sans"
                placeholder="Modify the clause text here..."
              />
            </div>
          </div>

          {/* Mandatory Reason */}
          <div>
            <label className="block text-xs font-bold text-amber-700 dark:text-amber-400 mb-1 flex items-center gap-1.5">
              <span>* Mandatory Legal Justification / Modification Reason:</span>
            </label>
            <textarea
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              required
              rows={3}
              placeholder="Provide clear legal or business justification (e.g. adjust SLA guarantees from 99.9% to 99.95% per customer risk committee)..."
              className="w-full p-3.5 text-xs input-corporate border-amber-300 dark:border-amber-700 focus:ring-amber-500 leading-relaxed"
            />
          </div>

          {/* Action Footer */}
          <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-100 dark:border-slate-800">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-xs font-semibold rounded-lg btn-corporate-secondary"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex items-center gap-2 px-5 py-2 text-xs font-bold rounded-lg btn-corporate-primary disabled:opacity-50"
            >
              <Send size={14} />
              <span>{loading ? 'Submitting...' : 'Submit for Review'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
