import { MobileNav } from "./MobileNav";
import { Sidebar } from "./Sidebar";
import { Toast } from "../ui/Toast";

const pageTitles = {
  home: "Home",
  find: "Cerca vicini",
  requests: "Richieste",
  neighbours: "Vicini",
  notifications: "Notifiche",
  profile: "Il mio profilo",
  blocked: "Utenti bloccati",
  admin: "Gestione località",
  settings: "Impostazioni",
  "post-detail": "Conversazione",
  "public-profile": "Profilo pubblico",
};

export function MainLayout({
  activePage,
  children,
  currentUser,
  metrics,
  onDismissToast,
  onLogout,
  onNavigate,
  toast,
}) {
  return (
    <div className="app-shell">
      <Sidebar
        activePage={activePage}
        currentUser={currentUser}
        isAdmin={currentUser?.admin}
        metrics={metrics}
        onLogout={onLogout}
        onNavigate={onNavigate}
      />
      <div className="main-column">
        <header className="main-header">
          <h1>{pageTitles[activePage] ?? "Rione"}</h1>
        </header>
        <Toast message={toast?.message} tone={toast?.tone} onClose={onDismissToast} />
        <main className="page-content">{children}</main>
      </div>
      <MobileNav activePage={activePage} onNavigate={onNavigate} />
    </div>
  );
}
