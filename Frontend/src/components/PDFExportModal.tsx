import React, { useState } from 'react';
import { X, Download, FileText, CheckCircle } from 'lucide-react';
import { contractsApi } from '../api/client';
import { VersionResponse } from '../types';

interface PDFExportModalProps {
  contractId: string;
  contractTitle: string;
  versions: VersionResponse[];
  currentVersionNumber: number;
  onClose: () => void;
}

export default function PDFExportModal({ contractId, contractTitle, versions, currentVersionNumber, onClose }: PDFExportModalProps) {
  const [selectedVersion, setSelectedVersion] = useState<number>(currentVersionNumber);
  const [downloading, setDownloading] = useState(false);

  const handleDownload = async () => {
    setDownloading(true);
    try {
      const response = await contractsApi.exportPdf(contractId, selectedVersion);
      const url = window.URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `${contractTitle.replace(/\s+/g, '_')}_v${selectedVersion}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      onClose();
    } catch (e) {
      console.error('PDF Download failed', e);
      alert('Failed to download PDF export. Please try again.');
    } finally {
      setDownloading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center z-50 p-4 animate-fade-in">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-corporate-hover w-full max-w-md p-6 relative">
        {/* Header */}
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-indigo-500 to-violet-600 flex items-center justify-center text-white shadow-corporate-btn">
              <FileText size={20} />
            </div>
            <div>
              <h3 className="text-base font-bold text-slate-900 dark:text-white">Export Contract to PDF</h3>
              <p className="text-xs text-slate-500 dark:text-slate-400">Official watermarked legal export</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
          >
            <X size={18} />
          </button>
        </div>

        <p className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed mb-5">
          Select the contract version you want to export. The generated document contains cryptographic timestamps, clause audit details, and party signatures.
        </p>

        {/* Version List */}
        <div className="mb-6 space-y-2">
          <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 uppercase tracking-wider">
            Available Versions:
          </label>
          <div className="space-y-2 max-h-52 overflow-y-auto pr-1">
            {versions.map((v) => {
              const isSelected = selectedVersion === v.versionNumber;
              return (
                <div
                  key={v.id}
                  onClick={() => setSelectedVersion(v.versionNumber)}
                  className={`flex items-center justify-between p-3 rounded-xl cursor-pointer border transition-all ${
                    isSelected
                      ? 'bg-indigo-50/80 dark:bg-indigo-950/50 border-indigo-300 dark:border-indigo-700 shadow-sm'
                      : 'bg-slate-50/50 dark:bg-slate-800/40 border-slate-200 dark:border-slate-800 hover:bg-slate-100/70 dark:hover:bg-slate-800/70'
                  }`}
                >
                  <div className="flex items-center gap-2.5">
                    <span className={`font-mono text-xs font-bold px-2 py-0.5 rounded ${
                      isSelected
                        ? 'bg-indigo-600 text-white'
                        : 'bg-slate-200 dark:bg-slate-700 text-slate-700 dark:text-slate-300'
                    }`}>
                      v{v.versionNumber}
                    </span>
                    <div>
                      <span className="text-xs font-semibold text-slate-800 dark:text-slate-200">
                        Version {v.versionNumber}
                      </span>
                      <span className="text-[11px] text-slate-500 dark:text-slate-400 ml-2">
                        • {v.status}
                      </span>
                    </div>
                  </div>
                  {isSelected && <CheckCircle size={17} className="text-indigo-600 dark:text-indigo-400" />}
                </div>
              );
            })}
          </div>
        </div>

        {/* Modal Actions */}
        <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-100 dark:border-slate-800">
          <button
            onClick={onClose}
            className="px-4 py-2 text-xs font-semibold rounded-lg btn-corporate-secondary"
          >
            Cancel
          </button>
          <button
            onClick={handleDownload}
            disabled={downloading}
            className="flex items-center gap-2 px-4 py-2 text-xs font-semibold rounded-lg btn-corporate-primary disabled:opacity-50"
          >
            <Download size={14} />
            <span>{downloading ? 'Generating PDF...' : 'Download PDF'}</span>
          </button>
        </div>
      </div>
    </div>
  );
}
