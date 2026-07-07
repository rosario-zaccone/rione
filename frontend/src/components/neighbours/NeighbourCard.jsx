import { formatDate, fullName, username } from "./personUtils";
import { Avatar } from "../ui/Avatar";
import { Button } from "../ui/Button";
import { Card } from "../ui/Card";

export function NeighbourCard({
  currentUser,
  loading,
  neighbor,
  onBlock,
  onOpenUserProfile,
  onRemove,
  user,
}) {
  const counterpartId = neighbor.userId === currentUser?.id ? neighbor.neighborId : neighbor.userId;

  return (
    <Card className="person-card" as="article">
      <div className="person-heading">
        <Avatar user={user} />
        <div>
          {user ? (
            <button className="profile-link" type="button" onClick={() => onOpenUserProfile(user)}>
              @{user.username}
            </button>
          ) : (
            <h3>{fullName(user, counterpartId)}</h3>
          )}
          <p>{user ? fullName(user) : username(user, counterpartId)}</p>
        </div>
      </div>
      <p className="muted">Connesso il {formatDate(neighbor.date)}</p>
      <div className="button-row">
        <Button variant="ghost" loading={loading} onClick={() => onRemove(counterpartId)}>
          Rimuovi vicino
        </Button>
        <Button variant="danger" loading={loading} onClick={() => onBlock(counterpartId)}>
          Blocca utente
        </Button>
      </div>
    </Card>
  );
}
