import { formatDate, fullName } from "../neighbours/personUtils";
import { Avatar } from "../ui/Avatar";
import { Button } from "../ui/Button";
import { Card } from "../ui/Card";

function typeLabel(type) {
  if (type === "REQUEST_RECEIVED") {
    return "Request received";
  }
  if (type === "REQUEST_ACCEPTED") {
    return "Request accepted";
  }
  return "Notification";
}

export function NotificationItem({ actor, loading, notification, onMarkRead }) {
  const unread = !notification.readAt;

  return (
    <Card className={unread ? "notification-item unread" : "notification-item"} as="article">
      <Avatar user={actor} />
      <div>
        <p className="eyebrow">{typeLabel(notification.type)}</p>
        <h3>{notification.title}</h3>
        <p>{notification.message}</p>
        <small>
          {fullName(actor, notification.actorId)} / {formatDate(notification.occurredAt)}
        </small>
      </div>
      {unread ? (
        <Button variant="secondary" loading={loading} onClick={() => onMarkRead(notification.id)}>
          Mark read
        </Button>
      ) : (
        <span className="read-label">Read {formatDate(notification.readAt)}</span>
      )}
    </Card>
  );
}
