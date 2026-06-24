import { MobileNav } from "./MobileNav";
import { RightPanel } from "./RightPanel";
import { Sidebar } from "./Sidebar";
import { TopNav } from "./TopNav";
import { Toast } from "../ui/Toast";

export function MainLayout({
  activePage,
  children,
  currentUser,
  metrics,
  neighborhoodLabel,
  onDismissToast,
  onLogout,
  onNavigate,
  onSearch,
  searchValue,
  toast,
}) {
  return (
    <div className="app-shell">
      <Sidebar activePage={activePage} isAdmin={currentUser?.admin} onNavigate={onNavigate} />
      <div className="main-column">
        <TopNav
          currentUser={currentUser}
          onFindNeighbours={() => onNavigate("find")}
          onLogout={onLogout}
          onOpenNotifications={() => onNavigate("notifications")}
          onSearch={onSearch}
          searchValue={searchValue}
          unreadCount={metrics.unread}
        />
        <Toast message={toast?.message} tone={toast?.tone} onClose={onDismissToast} />
        <main className="page-content">{children}</main>
      </div>
      <RightPanel
        currentUser={currentUser}
        metrics={metrics}
        neighborhoodLabel={neighborhoodLabel}
      />
      <MobileNav activePage={activePage} onNavigate={onNavigate} />
    </div>
  );
}
