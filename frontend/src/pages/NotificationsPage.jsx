import { NotificationList } from "../components/notifications/NotificationList";

export function NotificationsPage({
  knownUsers,
  notifications,
  onOpenPost,
  onOpenRequest,
  onOpenUserProfile,
}) {
  return (
    <NotificationList
      knownUsers={knownUsers}
      loading={notifications.loading}
      notifications={notifications.notifications}
      onMarkRead={notifications.markRead}
      onOpenPost={onOpenPost}
      onOpenRequest={onOpenRequest}
      onOpenUserProfile={onOpenUserProfile}
    />
  );
}
