import { Icon } from "../ui/Icons";

const mobileItems = [
  { id: "home", label: "Home", icon: "home" },
  { id: "find", label: "Cerca", icon: "search" },
  { id: "requests", label: "Richieste", icon: "requests" },
  { id: "notifications", label: "Avvisi", icon: "bell" },
  { id: "profile", label: "Profilo", icon: "profile" },
];

export function MobileNav({ activePage, onNavigate }) {
  return (
    <nav className="mobile-nav" aria-label="Navigazione mobile">
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
