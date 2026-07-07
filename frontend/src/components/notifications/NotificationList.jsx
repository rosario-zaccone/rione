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
        <p className="eyebrow">Notifiche</p>
        <h1>Aggiornamenti dal tuo quartiere</h1>
        <p>Qui trovi richieste, commenti e reazioni dei tuoi vicini.</p>
      </div>
      {loading ? (
        <Skeleton lines={4} />
      ) : notifications.length === 0 ? (
        <EmptyState title="Non hai ancora notifiche.">
          Le nuove attività relative al tuo profilo, alle richieste e ai post appariranno qui.
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
