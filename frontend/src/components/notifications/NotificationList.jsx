import { NotificationItem } from "./NotificationItem";
import { EmptyState } from "../ui/EmptyState";
import { Skeleton } from "../ui/Skeleton";

const supportedTypes = new Set(["REQUEST_RECEIVED", "REQUEST_ACCEPTED"]);

export function NotificationList({ knownUsers, loading, notifications, onMarkRead }) {
  const visibleNotifications = notifications.filter((notification) =>
    supportedTypes.has(notification.type),
  );

  return (
    <section className="page-grid">
      <div className="page-intro card">
        <p className="eyebrow">Notifications</p>
        <h1>Request updates</h1>
        <p>
          Supported notification types are REQUEST_RECEIVED and REQUEST_ACCEPTED. Unsupported feed,
          post, comment, reaction, or message notifications are not shown.
        </p>
      </div>
      {loading ? (
        <Skeleton lines={4} />
      ) : visibleNotifications.length === 0 ? (
        <EmptyState title="You have no notifications yet.">
          When neighbours send or accept requests, updates will appear here.
        </EmptyState>
      ) : (
        <div className="card-list">
          {visibleNotifications.map((notification) => (
            <NotificationItem
              actor={knownUsers.get(notification.actorId)}
              key={notification.id}
              loading={loading}
              notification={notification}
              onMarkRead={onMarkRead}
            />
          ))}
        </div>
      )}
    </section>
  );
}
