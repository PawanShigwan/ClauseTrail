export type Role = 'ADMIN' | 'EDITOR' | 'REVIEWER' | 'VIEWER';

export type ContractStatus = 'DRAFT' | 'PENDING_REVIEW' | 'APPROVED' | 'REJECTED' | 'ARCHIVED';

export type VersionStatus = 'PENDING_REVIEW' | 'APPROVED' | 'REJECTED';

export type ChangeType = 'ADDED' | 'MODIFIED' | 'DELETED' | 'UNCHANGED';

export type AuditAction = 
  | 'LOGIN'
  | 'USER_REGISTER'
  | 'ROLE_CHANGE'
  | 'UPLOAD_CONTRACT'
  | 'VIEW_CONTRACT'
  | 'UPDATE_METADATA'
  | 'MODIFY_CLAUSE'
  | 'SUBMIT_FOR_REVIEW'
  | 'APPROVE_MODIFICATION'
  | 'REJECT_MODIFICATION'
  | 'ARCHIVE_CONTRACT'
  | 'EXPORT_PDF'
  | 'EXPORT_AUDIT_LOG';

export type NotificationType = 
  | 'MODIFICATION_SUBMITTED'
  | 'MODIFICATION_APPROVED'
  | 'MODIFICATION_REJECTED'
  | 'CONTRACT_UPLOADED'
  | 'ROLE_ASSIGNED';

export interface UserReference {
  id: string;
  name: string;
  email: string;
  role: Role;
}

export interface User {
  id: string;
  name: string;
  email: string;
  role: Role;
  department?: string;
  title?: string;
  active: boolean;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  id: string;
  name: string;
  email: string;
  role: Role;
  department?: string;
  title?: string;
}

export interface Clause {
  id: string;
  orderIndex: number;
  clauseNumber: string;
  title: string;
  content: string;
  category?: string;
}

export interface ClauseDTO {
  id?: string;
  orderIndex?: number;
  clauseNumber: string;
  title: string;
  content: string;
  category?: string;
}

export interface ClauseChange {
  clauseId: string;
  clauseNumber: string;
  clauseTitle: string;
  previousText: string;
  modifiedText: string;
  changeType: ChangeType;
  reason?: string;
}

export type ClauseChangeDTO = ClauseChange;

export interface ContractVersion {
  id: string;
  contractId: string;
  versionNumber: number;
  fullText?: string;
  clauses: Clause[];
  clauseChanges?: ClauseChange[];
  modifiedBy?: UserReference;
  modificationReason?: string;
  status: VersionStatus;
  reviewedBy?: UserReference;
  reviewComments?: string;
  reviewedAt?: string;
  parentVersionId?: string;
  parentVersionNumber?: number;
  createdAt: string;
}

export type VersionResponse = ContractVersion;

export interface Contract {
  id: string;
  title: string;
  contractType: string;
  parties: string[];
  description?: string;
  tags: string[];
  currentVersionNumber: number;
  currentVersionId: string;
  pendingVersionNumber?: number;
  pendingVersionId?: string;
  status: ContractStatus;
  createdBy?: UserReference;
  lastModifiedBy?: UserReference;
  effectiveDate?: string;
  expirationDate?: string;
  contractValue?: string;
  createdAt: string;
  updatedAt: string;
  activeVersion?: ContractVersion;
  pendingVersion?: ContractVersion;
}

export type ContractResponse = Contract;

export interface DiffSegment {
  type: ChangeType;
  text: string;
}

export type DiffSegmentDTO = DiffSegment;

export interface ClauseDiff {
  clauseId: string;
  clauseNumber: string;
  clauseTitle: string;
  v1Text: string;
  v2Text: string;
  segments: DiffSegment[];
  changeSummary: ClauseChange;
}

export interface DiffResponse {
  contractId: string;
  contractTitle: string;
  v1Number: number;
  v2Number: number;
  v1: ContractVersion;
  v2: ContractVersion;
  clauseDiffs: ClauseDiff[];
  fullTextDiffSegments: DiffSegment[];
  totalClausesChanged: number;
  additionsCount: number;
  deletionsCount: number;
}

export interface ReviewQueueItem {
  contractId: string;
  contractTitle: string;
  contractType: string;
  parties: string[];
  versionId: string;
  versionNumber: number;
  parentVersionNumber: number;
  modificationReason: string;
  modifiedBy: UserReference;
  submittedAt: string;
  clausesChangedCount: number;
  changesPreview: ClauseChange[];
}

export type ReviewQueueItemDTO = ReviewQueueItem;

export interface AuditLog {
  id: string;
  contractId?: string;
  contractTitle?: string;
  userId: string;
  userName: string;
  userRole?: Role;
  action: AuditAction;
  details: string;
  versionNumber?: number;
  ipAddress?: string;
  timestamp: string;
}

export type AuditLogResponse = AuditLog;

export interface NotificationItem {
  id: string;
  userId: string;
  title: string;
  message: string;
  type: NotificationType;
  contractId?: string;
  contractTitle?: string;
  versionNumber?: number;
  read: boolean;
  createdAt: string;
}

export type NotificationResponse = NotificationItem;
