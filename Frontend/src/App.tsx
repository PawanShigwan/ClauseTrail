import React, { useState } from 'react';
import { useAuth } from './context/AuthContext';
import { NotificationProvider } from './context/NotificationContext';
import Navbar from './components/Navbar';
import Sidebar from './components/Sidebar';
import AuthView from './views/AuthView';
import DashboardView from './views/DashboardView';
import ContractDetailView from './views/ContractDetailView';
import ReviewQueueView from './views/ReviewQueueView';
import DiffComparisonView from './views/DiffComparisonView';
import VersionHistoryTimelineView from './views/VersionHistoryTimelineView';
import AuditLogView from './views/AuditLogView';
import AdminUserManagementView from './views/AdminUserManagementView';
import NewContractModal from './views/NewContractModal';

export default function App() {
  const { user, isLoading } = useAuth();
  const [currentView, setCurrentView] = useState<string>('dashboard');
  const [activeContractId, setActiveContractId] = useState<string>('');
  const [showNewContractModal, setShowNewContractModal] = useState<boolean>(false);

  if (isLoading) {
    return (
      <div style={{
        height: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center',
        background: 'var(--bg-primary)', color: 'var(--text-secondary)'
      }}>
        <div style={{ textAlign: 'center' }}>
          <div style={{
            width: '40px', height: '40px', border: '3px solid rgba(99,102,241,0.3)',
            borderTop: '3px solid #6366f1', borderRadius: '50%',
            animation: 'spin 1s linear infinite', margin: '0 auto 1rem'
          }} />
          <div>Initializing ClauseTrail...</div>
        </div>
      </div>
    );
  }

  if (!user) {
    return <AuthView />;
  }

  const handleNavigate = (view: string, id?: string) => {
    if (view === 'new-contract') {
      setShowNewContractModal(true);
      return;
    }
    if (id) {
      setActiveContractId(id);
    }
    setCurrentView(view);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const renderView = () => {
    switch (currentView) {
      case 'dashboard':
        return <DashboardView onNavigate={handleNavigate} />;
      case 'contract':
        return <ContractDetailView contractId={activeContractId} onNavigate={handleNavigate} />;
      case 'review-queue':
        return <ReviewQueueView onNavigate={handleNavigate} />;
      case 'diff':
        return <DiffComparisonView contractIdWithQuery={activeContractId} onNavigate={handleNavigate} />;
      case 'timeline':
        return <VersionHistoryTimelineView contractId={activeContractId} onNavigate={handleNavigate} />;
      case 'audit-logs':
        return <AuditLogView />;
      case 'users':
        return <AdminUserManagementView />;
      default:
        return <DashboardView onNavigate={handleNavigate} />;
    }
  };

  return (
    <NotificationProvider>
      <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', background: 'var(--bg-primary)' }}>
        {/* Top Navigation */}
        <Navbar currentView={currentView} onNavigate={handleNavigate} />

        <div style={{ display: 'flex', flex: 1 }}>
          {/* Left Sidebar */}
          <Sidebar currentView={currentView} onNavigate={handleNavigate} />

          {/* Main Content Area */}
          <main style={{ flex: 1, overflowX: 'hidden', paddingBottom: '3rem' }}>
            {renderView()}
          </main>
        </div>

        {/* Modal for creating a new contract */}
        {showNewContractModal && (
          <NewContractModal
            onClose={() => setShowNewContractModal(false)}
            onSuccess={(newId) => {
              setShowNewContractModal(false);
              handleNavigate('contract', newId);
            }}
          />
        )}
      </div>
    </NotificationProvider>
  );
}
