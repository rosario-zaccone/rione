import { formatDate, fullName } from "../neighbours/personUtils";
import { Avatar } from "../ui/Avatar";
import { Button } from "../ui/Button";
import { Card } from "../ui/Card";

function typeLabel(type) {
  if (type === "REQUEST_RECEIVED") {
    return "Richiesta di vicinato";
  }
  if (type === "REQUEST_ACCEPTED") {
    return "Richiesta accettata";
  }
  if (type === "POST_COMMENT_ADDED") {
    return "Nuovo commento";
  }
  if (type === "POST_REACTION_ADDED") {
    return "Nuova reazione";
  }
  return "Aggiornamento";
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
              Apri post
            </Button>
          ) : null}
          {isRequestNotification(notification) && notification.requestId ? (
            <Button variant="ghost" type="button" onClick={() => onOpenRequest(notification)}>
              Apri richiesta
            </Button>
          ) : null}
        </div>
      </div>
      {unread ? (
        <Button variant="secondary" loading={loading} onClick={() => onMarkRead(notification.id)}>
          Segna come letta
        </Button>
      ) : (
        <span className="read-label">Letta il {formatDate(notification.readAt)}</span>
      )}
    </Card>
  );
}
