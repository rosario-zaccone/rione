import { formatDate, fullName, username } from "./personUtils";
import { Avatar } from "../ui/Avatar";
import { Badge } from "../ui/Badge";
import { Button } from "../ui/Button";
import { Card } from "../ui/Card";

export function RequestCard({ loading, mode, onAccept, onDecline, onOpenUserProfile, request, user }) {
  const isPending = request.status === "PENDING";

  return (
    <Card className="request-card" as="article">
      <Avatar user={user} />
      <div>
        {user ? (
          <button className="profile-link" type="button" onClick={() => onOpenUserProfile(user)}>
            @{user.username}
          </button>
        ) : (
          <h3>{fullName(user, mode === "received" ? request.senderId : request.receiverId)}</h3>
        )}
        <p>{user ? fullName(user) : username(user, mode === "received" ? request.senderId : request.receiverId)}</p>
        <small>{formatDate(request.date)}</small>
      </div>
      <Badge tone={isPending ? "warning" : "success"}>{request.status}</Badge>
      {mode === "received" && isPending ? (
        <div className="button-row">
          <Button loading={loading} onClick={() => onAccept(request.id)}>
            Accept
          </Button>
          <Button variant="ghost" loading={loading} onClick={() => onDecline(request.id)}>
            Decline
          </Button>
        </div>
      ) : null}
    </Card>
  );
}
