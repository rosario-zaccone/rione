import { Icon } from "../ui/Icons";
import { IconButton } from "../ui/IconButton";

export function NotificationBell({ onClick, unreadCount }) {
  return (
    <IconButton label={`${unreadCount} notifiche non lette`} onClick={onClick}>
      <Icon name="bell" />
      {unreadCount > 0 ? <span className="notification-dot">{unreadCount}</span> : null}
    </IconButton>
  );
}
