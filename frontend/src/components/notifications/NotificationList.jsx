import { NotificationItem } from "./NotificationItem";
import { EmptyState } from "../ui/EmptyState";
import { Skeleton } from "../ui/Skeleton";

export function NotificationList({
  knownUsers,
  loading,
  notifications,
  onMarkRead,
  onOpenPost,
  onOpenRequest,
  onOpenUserProfile,
}) {
  return (
    <section className="page-grid">
      <div className="page-intro card">
        <p className="eyebrow">Notifications</p>
        <h1>Updates from your neighbourhood</h1>
        <p>Requests, comments, and reactions from your neighbours are collected here.</p>
      </div>
      {loading ? (
        <Skeleton lines={4} />
      ) : notifications.length === 0 ? (
        <EmptyState title="You have no notifications yet.">
          New activity around your profile, requests, and posts will appear here.
        </EmptyState>
      ) : (
        <div className="card-list">
          {notifications.map((notification) => (
            <NotificationItem
              actor={knownUsers.get(notification.actorId)}
              key={notification.id}
              loading={loading}
              notification={notification}
              onMarkRead={onMarkRead}
              onOpenPost={onOpenPost}
              onOpenRequest={onOpenRequest}
              onOpenUserProfile={onOpenUserProfile}
            />
          ))}
        </div>
      )}
    </section>
  );
}
