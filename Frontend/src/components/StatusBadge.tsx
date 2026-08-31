import React from 'react';
import { ContractStatus, VersionStatus } from '../types';
import { CheckCircle2, Clock, FileText, XCircle, Archive } from 'lucide-react';

interface StatusBadgeProps {
  status: ContractStatus | VersionStatus;
  size?: 'sm' | 'md' | 'lg';
  showIcon?: boolean;
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({
  status,
  size = 'md',
  showIcon = true,
}) => {
  const getBadgeConfig = () => {
    switch (status) {
      case 'APPROVED':
        return {
          label: 'Approved',
          bg: 'bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-500/15 dark:text-emerald-300 dark:border-emerald-500/30',
          dot: 'bg-emerald-500 ring-2 ring-emerald-200 dark:ring-emerald-900',
          icon: CheckCircle2,
        };
      case 'PENDING_REVIEW':
        return {
          label: 'Pending Review',
          bg: 'bg-amber-50 text-amber-800 border-amber-200 dark:bg-amber-500/15 dark:text-amber-300 dark:border-amber-500/30',
          dot: 'bg-amber-500 animate-pulse ring-2 ring-amber-200 dark:ring-amber-900',
          icon: Clock,
        };
      case 'REJECTED':
        return {
          label: 'Rejected',
          bg: 'bg-rose-50 text-rose-700 border-rose-200 dark:bg-rose-500/15 dark:text-rose-300 dark:border-rose-500/30',
          dot: 'bg-rose-500 ring-2 ring-rose-200 dark:ring-rose-900',
          icon: XCircle,
        };
      case 'DRAFT':
        return {
          label: 'Draft',
          bg: 'bg-indigo-50 text-indigo-700 border-indigo-200 dark:bg-indigo-500/15 dark:text-indigo-300 dark:border-indigo-500/30',
          dot: 'bg-indigo-500 ring-2 ring-indigo-200 dark:ring-indigo-900',
          icon: FileText,
        };
      case 'ARCHIVED':
        return {
          label: 'Archived',
          bg: 'bg-slate-100 text-slate-600 border-slate-200 dark:bg-slate-800 dark:text-slate-400 dark:border-slate-700',
          dot: 'bg-slate-400 ring-2 ring-slate-200 dark:ring-slate-800',
          icon: Archive,
        };
      default:
        return {
          label: status,
          bg: 'bg-slate-100 text-slate-700 border-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:border-slate-700',
          dot: 'bg-slate-400 ring-2 ring-slate-200 dark:ring-slate-800',
          icon: FileText,
        };
    }
  };

  const config = getBadgeConfig();
  const Icon = config.icon;

  const sizeClasses = {
    sm: 'text-xs px-2 py-0.5 gap-1.5',
    md: 'text-xs px-2.5 py-1 gap-1.5 font-medium',
    lg: 'text-sm px-3 py-1.5 gap-2 font-medium',
  };

  const iconSizes = {
    sm: 12,
    md: 14,
    lg: 16,
  };

  return (
    <span
      className={`inline-flex items-center rounded-full border backdrop-blur-md transition-colors ${config.bg} ${sizeClasses[size]}`}
    >
      <span className={`w-1.5 h-1.5 rounded-full ${config.dot}`} />
      {showIcon && <Icon size={iconSizes[size]} />}
      <span>{config.label}</span>
    </span>
  );
};

export default StatusBadge;
