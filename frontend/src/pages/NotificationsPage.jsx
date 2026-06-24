import { NotificationList } from "../components/notifications/NotificationList";

export function NotificationsPage({ knownUsers, notifications }) {
  return (
    <NotificationList
      knownUsers={knownUsers}
      loading={notifications.loading}
      notifications={notifications.notifications}
      onMarkRead={notifications.markRead}
    />
  );
}
