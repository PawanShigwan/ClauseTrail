import React, { useState } from 'react';
import { DiffResponse, DiffSegmentDTO } from '../types';
import { Columns, AlignLeft, ArrowRight, CheckCircle, FileText } from 'lucide-react';

interface DiffVisualizerProps {
  diff: DiffResponse;
  viewModeDefault?: 'side-by-side' | 'inline';
}

export default function DiffVisualizer({ diff, viewModeDefault = 'side-by-side' }: DiffVisualizerProps) {
  const [viewMode, setViewMode] = useState<'side-by-side' | 'inline'>(viewModeDefault);

  const renderSegments = (segments: DiffSegmentDTO[]) => {
    return segments.map((seg, idx) => {
      if (seg.type === 'ADDED') {
        return (
          <span key={idx} className="diff-added inline-block" title="Added text">
            {seg.text}
          </span>
        );
      }
      if (seg.type === 'DELETED') {
        return (
          <span key={idx} className="diff-deleted inline-block" title="Deleted text">
            {seg.text}
          </span>
        );
      }
      return <span key={idx} className="diff-unchanged">{seg.text}</span>;
    });
  };

  return (
    <div className="space-y-4">
      {/* Diff Header Bar */}
      <div className="card-corporate p-4 flex flex-wrap items-center justify-between gap-3">
        <div className="flex flex-wrap items-center gap-3">
          {/* Version Chips */}
          <div className="flex items-center gap-2">
            <span className="px-2.5 py-1 rounded-md text-xs font-bold font-mono bg-indigo-50 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-800">
              v{diff.v1Number} (Baseline)
            </span>
            <ArrowRight size={14} className="text-slate-400" />
            <span className="px-2.5 py-1 rounded-md text-xs font-bold font-mono bg-violet-50 dark:bg-violet-950/60 text-violet-700 dark:text-violet-300 border border-violet-200 dark:border-violet-800">
              v{diff.v2Number} (Revision)
            </span>
          </div>

          {/* Diff Metrics */}
          <div className="flex items-center gap-2.5 text-xs">
            <span className="px-2 py-0.5 rounded bg-emerald-50 dark:bg-emerald-950/50 text-emerald-700 dark:text-emerald-300 font-semibold border border-emerald-200 dark:border-emerald-800">
              +{diff.additionsCount} words
            </span>
            <span className="px-2 py-0.5 rounded bg-rose-50 dark:bg-rose-950/50 text-rose-700 dark:text-rose-300 font-semibold border border-rose-200 dark:border-rose-800">
              -{diff.deletionsCount} words
            </span>
            <span className="text-slate-500 dark:text-slate-400 font-medium">
              ({diff.totalClausesChanged} {diff.totalClausesChanged === 1 ? 'clause' : 'clauses'} modified)
            </span>
          </div>
        </div>

        {/* View mode toggle */}
        <div className="flex items-center bg-slate-100 dark:bg-slate-800/80 p-1 rounded-lg border border-slate-200/80 dark:border-slate-700/60">
          <button
            onClick={() => setViewMode('side-by-side')}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-md text-xs font-semibold transition-all ${
              viewMode === 'side-by-side'
                ? 'btn-corporate-primary shadow-sm'
                : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200'
            }`}
          >
            <Columns size={13} />
            <span>Side-by-Side</span>
          </button>
          <button
            onClick={() => setViewMode('inline')}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-md text-xs font-semibold transition-all ${
              viewMode === 'inline'
                ? 'btn-corporate-primary shadow-sm'
                : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200'
            }`}
          >
            <AlignLeft size={13} />
            <span>Inline Diff</span>
          </button>
        </div>
      </div>

      {/* Clause by Clause Comparison */}
      <div className="space-y-4">
        {diff.clauseDiffs?.map((clauseDiff) => {
          const isModified = clauseDiff.changeSummary?.changeType === 'MODIFIED';
          const isAdded = clauseDiff.changeSummary?.changeType === 'ADDED';
          const isDeleted = clauseDiff.changeSummary?.changeType === 'DELETED';
          const isUnchanged = clauseDiff.changeSummary?.changeType === 'UNCHANGED';

          const badgeClasses = isAdded
            ? 'bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-950/60 dark:text-emerald-300 dark:border-emerald-800'
            : isDeleted
            ? 'bg-rose-50 text-rose-700 border-rose-200 dark:bg-rose-950/60 dark:text-rose-300 dark:border-rose-800'
            : isModified
            ? 'bg-amber-50 text-amber-800 border-amber-200 dark:bg-amber-950/60 dark:text-amber-300 dark:border-amber-800'
            : 'bg-slate-100 text-slate-600 border-slate-200 dark:bg-slate-800 dark:text-slate-400 dark:border-slate-700';

          return (
            <div
              key={clauseDiff.clauseId || clauseDiff.clauseNumber}
              className="card-corporate overflow-hidden"
            >
              {/* Clause Header */}
              <div className="px-4 py-3 bg-slate-50/70 dark:bg-slate-800/40 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between gap-3">
                <div className="flex items-center gap-2.5">
                  <span className="font-mono text-xs font-bold px-2 py-0.5 rounded bg-indigo-50 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-800">
                    {clauseDiff.clauseNumber}
                  </span>
                  <span className="font-bold text-sm text-slate-900 dark:text-slate-100">
                    {clauseDiff.clauseTitle}
                  </span>
                </div>
                <span className={`px-2.5 py-0.5 rounded-full text-[11px] font-bold border uppercase tracking-wider ${badgeClasses}`}>
                  {clauseDiff.changeSummary?.changeType || 'UNCHANGED'}
                </span>
              </div>

              {/* Diff Content View */}
              <div className="p-4 sm:p-5">
                {viewMode === 'side-by-side' ? (
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {/* Previous (v1) */}
                    <div className={`p-3.5 rounded-lg border text-xs leading-relaxed font-sans ${
                      isDeleted || isModified
                        ? 'bg-rose-50/40 dark:bg-rose-950/20 border-rose-200 dark:border-rose-900/60'
                        : 'bg-slate-50 dark:bg-slate-800/40 border-slate-200 dark:border-slate-800'
                    }`}>
                      <div className="text-[11px] font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider mb-2 flex items-center justify-between">
                        <span>Original (v{diff.v1Number})</span>
                        {isAdded && <span className="text-slate-400 italic">Not in baseline</span>}
                      </div>
                      <div className="text-slate-700 dark:text-slate-300 whitespace-pre-wrap">
                        {clauseDiff.v1Text || (
                          <span className="italic text-slate-400">Clause did not exist in v{diff.v1Number}</span>
                        )}
                      </div>
                    </div>

                    {/* Modified (v2) */}
                    <div className={`p-3.5 rounded-lg border text-xs leading-relaxed font-sans ${
                      isAdded || isModified
                        ? 'bg-emerald-50/40 dark:bg-emerald-950/20 border-emerald-200 dark:border-emerald-900/60'
                        : 'bg-slate-50 dark:bg-slate-800/40 border-slate-200 dark:border-slate-800'
                    }`}>
                      <div className="text-[11px] font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider mb-2 flex items-center justify-between">
                        <span>Revision (v{diff.v2Number})</span>
                        {isDeleted && <span className="text-rose-500 font-semibold">Removed in revision</span>}
                      </div>
                      <div className="text-slate-900 dark:text-slate-100 whitespace-pre-wrap">
                        {clauseDiff.v2Text ? (
                          renderSegments(clauseDiff.segments || [])
                        ) : (
                          <span className="italic text-rose-500 dark:text-rose-400">Clause removed in v{diff.v2Number}</span>
                        )}
                      </div>
                    </div>
                  </div>
                ) : (
                  /* Inline Unified View */
                  <div className="p-4 rounded-lg bg-slate-50/80 dark:bg-slate-900/60 border border-slate-200 dark:border-slate-800 text-xs sm:text-sm leading-relaxed whitespace-pre-wrap">
                    {renderSegments(clauseDiff.segments || [])}
                  </div>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
