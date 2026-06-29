import { formatDate, fullName } from "../neighbours/personUtils";
import { Avatar } from "../ui/Avatar";
import { Button } from "../ui/Button";
import { Card } from "../ui/Card";

function typeLabel(type) {
  if (type === "REQUEST_RECEIVED") {
    return "Neighbour request";
  }
  if (type === "REQUEST_ACCEPTED") {
    return "Request accepted";
  }
  if (type === "POST_COMMENT_ADDED") {
    return "New comment";
  }
  if (type === "POST_REACTION_ADDED") {
    return "New reaction";
  }
  return "Update";
}

function relatedPostId(notification) {
  return notification.postId ?? (
    notification.type?.startsWith("POST_") ? notification.requestId : null
  );
}

function isPostNotification(notification) {
  return notification.type === "POST_COMMENT_ADDED" || notification.type === "POST_REACTION_ADDED";
}

function isRequestNotification(notification) {
  return notification.type === "REQUEST_RECEIVED" || notification.type === "REQUEST_ACCEPTED";
}

export function NotificationItem({
  actor,
  loading,
  notification,
  onMarkRead,
  onOpenPost,
  onOpenRequest,
  onOpenUserProfile,
}) {
  const unread = !notification.readAt;
  const postId = relatedPostId(notification);

  return (
    <Card className={unread ? "notification-item unread" : "notification-item"} as="article">
      <Avatar user={actor} />
      <div>
        <p className="eyebrow">{typeLabel(notification.type)}</p>
        <h3>{notification.title}</h3>
        <p>{notification.message}</p>
        <small>
          {actor ? (
            <button className="profile-link small" type="button" onClick={() => onOpenUserProfile(actor)}>
              {fullName(actor, notification.actorId)}
            </button>
          ) : (
            fullName(actor, notification.actorId)
          )}{" "}
          / {formatDate(notification.occurredAt)}
        </small>
        <div className="button-row">
          {isPostNotification(notification) && postId ? (
            <Button variant="ghost" type="button" onClick={() => onOpenPost(postId)}>
              Open post
            </Button>
          ) : null}
          {isRequestNotification(notification) && notification.requestId ? (
            <Button variant="ghost" type="button" onClick={() => onOpenRequest(notification)}>
              Open request
            </Button>
          ) : null}
        </div>
      </div>
      {unread ? (
        <Button variant="secondary" loading={loading} onClick={() => onMarkRead(notification.id)}>
          Mark as read
        </Button>
      ) : (
        <span className="read-label">Read {formatDate(notification.readAt)}</span>
      )}
    </Card>
  );
}
