import { Icon } from "../ui/Icons";
import { Avatar } from "../ui/Avatar";

export const navItems = [
  { id: "home", label: "Home", icon: "home" },
  { id: "profile", label: "Profile", icon: "profile" },
  { id: "find", label: "Search Neighbors", icon: "search" },
  { id: "neighbours", label: "Neighbours", icon: "neighbours" },
  { id: "blocked", label: "Blocked Users", icon: "blocked" },
  { id: "notifications", label: "Notifications", icon: "bell" },
];

function navBadge(item, metrics) {
  if (item.id === "neighbours") {
    return metrics?.received ? metrics.received : null;
  }
  if (item.id === "notifications") {
    return metrics?.unread ? metrics.unread : null;
  }
  return null;
}

export function Sidebar({ activePage, currentUser, isAdmin, metrics, onLogout, onNavigate }) {
  const items = isAdmin ? [...navItems, { id: "admin", label: "Admin Locations", icon: "admin" }] : navItems;
  const location = [currentUser?.neighborhoodName, currentUser?.city].filter(Boolean).join(" · ");

  return (
    <aside className="sidebar" aria-label="Main navigation">
      <div className="brand-lockup">
        <div>
          <strong>Rione</strong>
          <span className="brand-location">
            <Icon name="mapPin" />
            {location || "Neighbour network"}
          </span>
        </div>
      </div>
      <button className="sidebar-user-card" type="button" onClick={() => onNavigate("profile")}>
        <Avatar user={currentUser} />
        <span>
          <strong>{currentUser?.name} {currentUser?.surname}</strong>
          <small>@{currentUser?.username}</small>
        </span>
      </button>
      <nav className="sidebar-nav">
        {items.map((item) => {
          const badge = navBadge(item, metrics);
          const active = activePage === item.id || (activePage === "requests" && item.id === "neighbours");
          return (
            <button
              className={active ? "nav-link active" : "nav-link"}
              key={item.id}
              onClick={() => onNavigate(item.id)}
              type="button"
            >
              <Icon name={item.icon} />
              <span>{item.label}</span>
              {badge ? <strong className="nav-badge">{badge}</strong> : null}
            </button>
          );
        })}
      </nav>
      <div className="sidebar-footer">
        <button className="nav-link logout-link" onClick={onLogout} type="button">
          <Icon name="logout" />
          <span>Log out</span>
        </button>
      </div>
    </aside>
  );
}
