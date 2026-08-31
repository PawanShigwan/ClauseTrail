import { useEffect, useState } from 'react';
import { contractsApi } from '../api/client';
import { ContractResponse, VersionResponse, DiffResponse } from '../types';
import DiffVisualizer from '../components/DiffVisualizer';
import { ArrowLeft } from 'lucide-react';

interface DiffComparisonViewProps {
  contractIdWithQuery: string;
  onNavigate: (view: string, id?: string) => void;
}

export default function DiffComparisonView({ contractIdWithQuery, onNavigate }: DiffComparisonViewProps) {
  // Parse contractId and query params (?v1=1&v2=2)
  const [contractId, queryStr] = contractIdWithQuery.split('?');
  const params = new URLSearchParams(queryStr || '');
  const initialV1 = params.get('v1') ? parseInt(params.get('v1')!) : 1;
  const initialV2 = params.get('v2') ? parseInt(params.get('v2')!) : 2;

  const [contract, setContract] = useState<ContractResponse | null>(null);
  const [versions, setVersions] = useState<VersionResponse[]>([]);
  const [v1, setV1] = useState<number>(initialV1);
  const [v2, setV2] = useState<number>(initialV2);
  const [diff, setDiff] = useState<DiffResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      const [contractRes, versionsRes] = await Promise.all([
        contractsApi.getById(contractId),
        contractsApi.getVersionHistory(contractId)
      ]);
      setContract(contractRes.data);
      setVersions(versionsRes.data);

      // Auto-set versions if out of range
      const vList: VersionResponse[] = versionsRes.data;
      const effectiveV1 = vList.some((v: VersionResponse) => v.versionNumber === v1) ? v1 : (vList[0]?.versionNumber || 1);
      const effectiveV2 = vList.some((v: VersionResponse) => v.versionNumber === v2) ? v2 : (vList[vList.length - 1]?.versionNumber || effectiveV1);
      setV1(effectiveV1);
      setV2(effectiveV2);

      const diffRes = await contractsApi.getDiff(contractId, effectiveV1, effectiveV2);
      setDiff(diffRes.data);
    } catch (e: any) {
      console.error(e);
      setError(e?.response?.data?.message || 'Failed to load diff comparison.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [contractId]);

  const handleComputeDiff = async (newV1: number, newV2: number) => {
    setV1(newV1);
    setV2(newV2);
    setLoading(true);
    try {
      const diffRes = await contractsApi.getDiff(contractId, newV1, newV2);
      setDiff(diffRes.data);
    } catch (e: any) {
      setError(e?.response?.data?.message || 'Failed to compute diff.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto space-y-6">
      {/* Back button */}
      <button
        onClick={() => onNavigate('contract', contractId)}
        className="inline-flex items-center gap-1.5 text-xs font-bold text-slate-500 hover:text-indigo-600 dark:text-slate-400 dark:hover:text-indigo-300 transition-colors group"
      >
        <ArrowLeft size={15} className="group-hover:-translate-x-0.5 transition-transform" />
        <span>Back to Contract</span>
      </button>

      {/* Header Bar */}
      <div className="card-corporate p-6">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div>
            <h1 className="text-xl sm:text-2xl font-extrabold text-slate-900 dark:text-white tracking-tight">
              Version Comparison <span className="gradient-text">Studio</span>
            </h1>
            <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-0.5">
              {contract?.title || 'Contract'} — Side-by-side & inline word-level diff analysis
            </p>
          </div>

          {/* Version Selectors */}
          <div className="flex items-center gap-3 flex-wrap">
            <div>
              <label className="text-[11px] font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider block mb-1">
                Baseline (v1):
              </label>
              <select
                value={v1}
                onChange={(e) => handleComputeDiff(parseInt(e.target.value), v2)}
                className="px-3 py-1.5 text-xs font-bold font-mono input-corporate w-36"
              >
                {versions.map((v) => (
                  <option key={v.id} value={v.versionNumber}>
                    Version {v.versionNumber} ({v.status})
                  </option>
                ))}
              </select>
            </div>

            <div className="self-end pb-2 font-bold text-slate-400">
              vs
            </div>

            <div>
              <label className="text-[11px] font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider block mb-1">
                Revision (v2):
              </label>
              <select
                value={v2}
                onChange={(e) => handleComputeDiff(v1, parseInt(e.target.value))}
                className="px-3 py-1.5 text-xs font-bold font-mono input-corporate w-36"
              >
                {versions.map((v) => (
                  <option key={v.id} value={v.versionNumber}>
                    Version {v.versionNumber} ({v.status})
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>
      </div>

      {/* Diff Content */}
      {loading ? (
        <div className="py-20 text-center text-slate-400">
          <div className="w-8 h-8 border-2 border-indigo-600 border-t-transparent rounded-full animate-spin mx-auto mb-3" />
          <p className="text-xs font-medium">Computing word-level diff between v{v1} and v{v2}...</p>
        </div>
      ) : error ? (
        <div className="card-corporate p-8 text-center text-rose-600 dark:text-rose-400 font-semibold text-xs">
          {error}
        </div>
      ) : diff ? (
        <DiffVisualizer diff={diff} />
      ) : null}
    </div>
  );
}
