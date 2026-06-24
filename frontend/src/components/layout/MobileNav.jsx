import { Icon } from "../ui/Icons";

const mobileItems = [
  { id: "home", label: "Home", icon: "home" },
  { id: "find", label: "Find", icon: "search" },
  { id: "requests", label: "Requests", icon: "requests" },
  { id: "notifications", label: "Alerts", icon: "bell" },
  { id: "profile", label: "Profile", icon: "profile" },
];

export function MobileNav({ activePage, onNavigate }) {
  return (
    <nav className="mobile-nav" aria-label="Mobile navigation">
      {mobileItems.map((item) => (
        <button
          className={activePage === item.id ? "mobile-nav-link active" : "mobile-nav-link"}
          key={item.id}
          onClick={() => onNavigate(item.id)}
          type="button"
        >
          <Icon name={item.icon} />
          <span>{item.label}</span>
        </button>
      ))}
    </nav>
  );
}
