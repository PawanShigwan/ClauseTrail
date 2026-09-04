import React, { useState } from 'react';
import { contractsApi } from '../api/client';
import { ClauseDTO } from '../types';
import {
  FileText, Upload, Plus, Trash2, X, Check,
  Sparkles
} from 'lucide-react';

interface NewContractModalProps {
  onClose: () => void;
  onSuccess: (newContractId: string) => void;
}

const TEMPLATES = [
  {
    name: 'SaaS Master Services Agreement (MSA)',
    type: 'SaaS Agreement',
    parties: ['CloudTech Solutions Inc.', 'Enterprise Corp'],
    contractValue: '$120,000 / year',
    clauses: [
      {
        clauseNumber: '1.1',
        title: 'Subscription Services & Access',
        category: 'Services',
        content: 'Subject to the terms and conditions of this Agreement, Provider hereby grants to Customer a non-exclusive, non-transferable right to access and use the SaaS Services during the Term solely for Customer\'s internal business operations.'
      },
      {
        clauseNumber: '2.1',
        title: 'Fees and Payment Terms',
        category: 'Financial',
        content: 'Customer shall pay all fees specified in applicable Order Forms. All fees are quoted and payable in United States Dollars. Payment obligations are non-cancelable and fees paid are non-refundable. Invoices are due within thirty (30) days from invoice date.'
      },
      {
        clauseNumber: '3.1',
        title: 'Confidentiality Obligations',
        category: 'Confidentiality',
        content: 'Each party agrees that all code, inventions, business, technical and financial information disclosed to such party by the other party constitute confidential property of the disclosing party. The receiving party shall hold in confidence and not disclose such Confidential Information.'
      },
      {
        clauseNumber: '4.1',
        title: 'Limitation of Liability',
        category: 'Liability',
        content: 'IN NO EVENT SHALL EITHER PARTY BE LIABLE TO THE OTHER PARTY FOR ANY INDIRECT, SPECIAL, INCIDENTAL, CONSEQUENTIAL OR PUNITIVE DAMAGES, ARISING OUT OF OR IN CONNECTION WITH THIS AGREEMENT. THE MAXIMUM AGGREGATE LIABILITY SHALL BE LIMITED TO THE TOTAL AMOUNT PAID UNDER THIS AGREEMENT IN THE PRECEDING 12 MONTHS.'
      },
      {
        clauseNumber: '5.1',
        title: 'Governing Law and Jurisdiction',
        category: 'Legal',
        content: 'This Agreement shall be governed by and construed in accordance with the laws of the State of Delaware, without regard to its conflict of laws principles. Any legal action arising hereunder shall be instituted in federal or state courts situated in New Castle County, Delaware.'
      }
    ]
  },
  {
    name: 'Non-Disclosure Agreement (Mutual NDA)',
    type: 'Non-Disclosure Agreement',
    parties: ['Innovate AI Labs', 'Partner Global LLC'],
    contractValue: 'N/A',
    clauses: [
      {
        clauseNumber: '1.0',
        title: 'Definition of Confidential Information',
        category: 'Confidentiality',
        content: '"Confidential Information" means all non-public information, technical data, trade secrets, software code, customer lists, and financial forecasts disclosed by one Party to the other.'
      },
      {
        clauseNumber: '2.0',
        title: 'Standard of Care',
        category: 'Compliance',
        content: 'The Receiving Party agrees to protect the disclosing party\'s Confidential Information with the same degree of care it uses for its own confidential materials, but not less than reasonable care.'
      },
      {
        clauseNumber: '3.0',
        title: 'Term & Survival',
        category: 'Term',
        content: 'This Agreement shall remain in effect for a period of two (2) years from the Effective Date. Confidentiality obligations shall survive termination for an additional period of three (3) years.'
      }
    ]
  }
];

export default function NewContractModal({ onClose, onSuccess }: NewContractModalProps) {
  const [tab, setTab] = useState<'template' | 'upload' | 'scratch'>('template');
  const [title, setTitle] = useState('');
  const [contractType, setContractType] = useState('Commercial Contract');
  const [partyA, setPartyA] = useState('');
  const [partyB, setPartyB] = useState('');
  const [description] = useState('');
  const [effectiveDate] = useState(new Date().toISOString().slice(0, 10));
  const [contractValue, setContractValue] = useState('');
  const [clauses, setClauses] = useState<ClauseDTO[]>([
    { clauseNumber: '1.0', title: 'Scope of Agreement', category: 'General', content: '' }
  ]);
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const loadTemplate = (tmpl: typeof TEMPLATES[0]) => {
    setTitle(tmpl.name);
    setContractType(tmpl.type);
    setPartyA(tmpl.parties[0] || '');
    setPartyB(tmpl.parties[1] || '');
    setContractValue(tmpl.contractValue);
    setClauses(tmpl.clauses);
  };

  const handleAddClause = () => {
    const nextNum = (clauses.length + 1) + '.0';
    setClauses([...clauses, { clauseNumber: nextNum, title: 'New Clause', category: 'General', content: '' }]);
  };

  const handleRemoveClause = (idx: number) => {
    setClauses(clauses.filter((_, i) => i !== idx));
  };

  const handleClauseChange = (idx: number, field: keyof ClauseDTO, val: string) => {
    const updated = [...clauses];
    updated[idx] = { ...updated[idx], [field]: val };
    setClauses(updated);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const parties = [partyA.trim(), partyB.trim()].filter(Boolean);

      if (tab === 'upload' && file) {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('title', title || file.name);
        formData.append('contractType', contractType);
        formData.append('parties', parties.join(','));
        formData.append('description', description);

        const { data } = await contractsApi.upload(formData);
        onSuccess(data.id);
      } else {
        if (clauses.length === 0 || !clauses[0].content.trim()) {
          setError('Please provide at least one clause with content.');
          setLoading(false);
          return;
        }

        const { data } = await contractsApi.create({
          title,
          contractType,
          parties: parties.length ? parties : ['Party A', 'Party B'],
          description,
          effectiveDate,
          contractValue,
          clauses: clauses
        });

        onSuccess(data.id);
      }
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Failed to create contract.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-slate-900/70 backdrop-blur-sm flex items-center justify-center z-50 p-4 sm:p-6 animate-fade-in">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-corporate-hover w-full max-w-4xl max-h-[92vh] flex flex-col overflow-hidden text-left">
        {/* Header */}
        <div className="px-6 py-4 border-b border-slate-100 dark:border-slate-800 bg-slate-50/70 dark:bg-slate-800/40 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-indigo-600 to-violet-600 text-white flex items-center justify-center shadow-corporate-btn">
              <FileText size={18} />
            </div>
            <div>
              <h3 className="text-base font-bold text-slate-900 dark:text-white">Create New Contract</h3>
              <p className="text-xs text-slate-500 dark:text-slate-400">Initialize a legally binding agreement with immutable baseline (v1.0)</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
          >
            <X size={18} />
          </button>
        </div>

        {/* Mode Selector Tabs */}
        <div className="flex gap-2 px-6 pt-3.5 pb-2 bg-slate-50/50 dark:bg-slate-900/50 border-b border-slate-100 dark:border-slate-800">
          {[
            { id: 'template', label: 'Use Legal Template', icon: Sparkles },
            { id: 'upload', label: 'Upload Document (PDF/DOCX)', icon: Upload },
            { id: 'scratch', label: 'Draft from Scratch', icon: Plus },
          ].map((t) => {
            const Icon = t.icon;
            const active = tab === t.id;
            return (
              <button
                key={t.id}
                type="button"
                onClick={() => {
                  setTab(t.id as any);
                  if (t.id === 'template') loadTemplate(TEMPLATES[0]);
                }}
                className={`flex items-center gap-1.5 px-3.5 py-1.5 rounded-lg text-xs font-semibold transition-all ${
                  active
                    ? 'btn-corporate-primary shadow-sm'
                    : 'bg-white dark:bg-slate-800 text-slate-600 dark:text-slate-400 border border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-750'
                }`}
              >
                <Icon size={14} />
                <span>{t.label}</span>
              </button>
            );
          })}
        </div>

        {/* Body Form */}
        <form onSubmit={handleSubmit} className="p-6 overflow-y-auto space-y-4 flex-1">
          {error && (
            <div className="p-3 rounded-lg bg-rose-50 dark:bg-rose-950/50 border border-rose-200 dark:border-rose-800 text-rose-700 dark:text-rose-300 text-xs font-semibold">
              {error}
            </div>
          )}

          {/* Quick Template Chooser */}
          {tab === 'template' && (
            <div>
              <label className="block text-[11px] font-bold uppercase tracking-wider text-slate-400 mb-2">
                Select Pre-Configured Template:
              </label>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                {TEMPLATES.map((tmpl) => {
                  const selected = title === tmpl.name;
                  return (
                    <div
                      key={tmpl.name}
                      onClick={() => loadTemplate(tmpl)}
                      className={`p-3.5 rounded-xl cursor-pointer border transition-all ${
                        selected
                          ? 'border-indigo-600 bg-indigo-50/50 dark:bg-indigo-950/40 ring-1 ring-indigo-500'
                          : 'border-slate-200 dark:border-slate-700 hover:border-slate-300 bg-white dark:bg-slate-800/60'
                      }`}
                    >
                      <div className="font-bold text-xs text-slate-900 dark:text-slate-100 mb-1">{tmpl.name}</div>
                      <div className="text-[11px] text-slate-500 dark:text-slate-400">
                        {tmpl.clauses.length} standard clauses • {tmpl.type}
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* Upload File Box */}
          {tab === 'upload' && (
            <div className="border-2 border-dashed border-indigo-200 dark:border-indigo-800/80 rounded-2xl p-8 text-center bg-indigo-50/20 dark:bg-indigo-950/20 hover:bg-indigo-50/40 transition-colors">
              <input
                type="file"
                accept=".pdf,.docx,.txt"
                onChange={(e) => {
                  const f = e.target.files?.[0];
                  if (f) {
                    setFile(f);
                    if (!title) setTitle(f.name.replace(/\.[^/.]+$/, ''));
                  }
                }}
                className="hidden"
                id="contract-file-upload"
              />
              <label htmlFor="contract-file-upload" className="cursor-pointer block">
                <Upload size={36} className="text-indigo-600 dark:text-indigo-400 mx-auto mb-2" />
                <div className="font-bold text-sm text-slate-900 dark:text-slate-100 mb-1">
                  {file ? file.name : 'Click to select PDF or Word document'}
                </div>
                <div className="text-xs text-slate-400">
                  Supported extensions: .pdf, .docx, .txt (Max 25MB)
                </div>
              </label>
            </div>
          )}

          {/* Metadata Inputs */}
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-3.5">
            <div className="sm:col-span-2">
              <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">Contract Title</label>
              <input
                className="w-full px-3.5 py-2 text-xs input-corporate font-semibold"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                required
                placeholder="e.g. Master Services Agreement"
              />
            </div>
            <div>
              <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">Contract Type</label>
              <input
                className="w-full px-3.5 py-2 text-xs input-corporate font-semibold"
                value={contractType}
                onChange={(e) => setContractType(e.target.value)}
                placeholder="e.g. SaaS Agreement"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-3.5">
            <div>
              <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">Party A (First Party)</label>
              <input
                className="w-full px-3.5 py-2 text-xs input-corporate"
                value={partyA}
                onChange={(e) => setPartyA(e.target.value)}
                placeholder="e.g. Acme Corp"
              />
            </div>
            <div>
              <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">Party B (Second Party)</label>
              <input
                className="w-full px-3.5 py-2 text-xs input-corporate"
                value={partyB}
                onChange={(e) => setPartyB(e.target.value)}
                placeholder="e.g. Global Tech LLC"
              />
            </div>
            <div>
              <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">Contract Value</label>
              <input
                className="w-full px-3.5 py-2 text-xs input-corporate"
                value={contractValue}
                onChange={(e) => setContractValue(e.target.value)}
                placeholder="e.g. $120,000 / year"
              />
            </div>
          </div>

          {/* Clauses Editor (for template or scratch mode) */}
          {tab !== 'upload' && (
            <div className="space-y-3 pt-2">
              <div className="flex items-center justify-between">
                <label className="text-xs font-bold text-slate-900 dark:text-white">
                  Baseline Clauses ({clauses.length})
                </label>
                <button
                  type="button"
                  onClick={handleAddClause}
                  className="flex items-center gap-1 px-2.5 py-1 text-xs font-semibold rounded-md text-indigo-700 dark:text-indigo-300 bg-indigo-50 dark:bg-indigo-950/60 hover:bg-indigo-100 dark:hover:bg-indigo-900/60 border border-indigo-200 dark:border-indigo-800 transition-colors"
                >
                  <Plus size={13} />
                  <span>Add Clause</span>
                </button>
              </div>

              <div className="space-y-3">
                {clauses.map((c, idx) => (
                  <div
                    key={idx}
                    className="p-4 rounded-xl bg-slate-50/70 dark:bg-slate-800/40 border border-slate-200 dark:border-slate-700 space-y-2.5"
                  >
                    <div className="flex items-center gap-2">
                      <input
                        className="w-16 px-2 py-1.5 text-xs font-mono font-bold input-corporate"
                        placeholder="1.0"
                        value={c.clauseNumber}
                        onChange={(e) => handleClauseChange(idx, 'clauseNumber', e.target.value)}
                      />
                      <input
                        className="flex-1 px-3 py-1.5 text-xs font-bold input-corporate"
                        placeholder="Clause Title (e.g. Limitation of Liability)"
                        value={c.title}
                        onChange={(e) => handleClauseChange(idx, 'title', e.target.value)}
                      />
                      <input
                        className="w-32 px-3 py-1.5 text-xs input-corporate"
                        placeholder="Category"
                        value={c.category}
                        onChange={(e) => handleClauseChange(idx, 'category', e.target.value)}
                      />
                      {clauses.length > 1 && (
                        <button
                          type="button"
                          onClick={() => handleRemoveClause(idx)}
                          className="p-1 text-rose-500 hover:text-rose-700 dark:hover:text-rose-400"
                        >
                          <Trash2 size={16} />
                        </button>
                      )}
                    </div>
                    <textarea
                      className="w-full p-3 text-xs input-corporate leading-relaxed font-sans"
                      rows={3}
                      placeholder="Enter legal clause text..."
                      value={c.content}
                      onChange={(e) => handleClauseChange(idx, 'content', e.target.value)}
                    />
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Footer Buttons */}
          <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100 dark:border-slate-800">
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
              <Check size={15} />
              <span>{loading ? 'Initializing...' : 'Create & Establish Baseline (v1.0)'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
