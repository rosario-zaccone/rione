import { LogoutButton } from "../auth/LogoutButton";
import { NotificationBell } from "../notifications/NotificationBell";
import { Avatar } from "../ui/Avatar";
import { Button } from "../ui/Button";
import { SearchInput } from "../ui/SearchInput";

export function TopNav({
  currentUser,
  onFindNeighbours,
  onLogout,
  onOpenNotifications,
  onOpenProfile,
  onSearch,
  searchValue,
  unreadCount,
}) {
  return (
    <header className="top-nav">
      <div className="top-nav-title">
        <span className="brand-orb small">R</span>
        <div>
          <strong>Rione</strong>
          <span>Safe neighbourhood discovery</span>
        </div>
      </div>
      <SearchInput
        label="Search neighbours"
        placeholder="Search neighbours"
        value={searchValue}
        onChange={onSearch}
      />
      <div className="top-nav-actions">
        <Button variant="secondary" onClick={onFindNeighbours}>
          Find neighbours
        </Button>
        <NotificationBell unreadCount={unreadCount} onClick={onOpenNotifications} />
        <button className="profile-shortcut" type="button" aria-label="Open profile" onClick={onOpenProfile}>
          <Avatar user={currentUser} />
          <span>{currentUser?.username}</span>
        </button>
        <LogoutButton onLogout={onLogout} />
      </div>
    </header>
  );
}
