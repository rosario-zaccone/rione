import { Icon } from "../ui/Icons";

export const navItems = [
  { id: "home", label: "Home", icon: "home" },
  { id: "find", label: "Find Neighbours", icon: "search" },
  { id: "requests", label: "Requests", icon: "requests" },
  { id: "neighbours", label: "My Neighbours", icon: "neighbours" },
  { id: "notifications", label: "Notifications", icon: "bell" },
  { id: "profile", label: "Profile", icon: "profile" },
  { id: "blocked", label: "Blocked Users", icon: "blocked" },
  { id: "settings", label: "Settings", icon: "settings" },
];

export function Sidebar({ activePage, isAdmin, onNavigate }) {
  const items = isAdmin
    ? [...navItems, { id: "admin", label: "Admin Locations", icon: "admin" }]
    : navItems;

  return (
    <aside className="sidebar" aria-label="Main navigation">
      <div className="brand-lockup">
        <span className="brand-orb">R</span>
        <div>
          <strong>Rione</strong>
          <span>Neighbour network</span>
        </div>
      </div>
      <nav className="sidebar-nav">
        {items.map((item) => (
          <button
            className={activePage === item.id ? "nav-link active" : "nav-link"}
            key={item.id}
            onClick={() => onNavigate(item.id)}
            type="button"
          >
            <Icon name={item.icon} />
            <span>{item.label}</span>
          </button>
        ))}
      </nav>
    </aside>
  );
}
