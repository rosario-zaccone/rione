import { fullName, username } from "./personUtils";
import { Avatar } from "../ui/Avatar";
import { Badge } from "../ui/Badge";
import { Button } from "../ui/Button";
import { Card } from "../ui/Card";

export function NeighbourSearchResultCard({
  currentUser,
  loading,
  onOpenUserProfile,
  onSendRequest,
  person,
}) {
  const isSelf = person.id === currentUser?.id;

  return (
    <Card className="person-card" as="article">
      <div className="person-heading">
        <Avatar user={person} />
        <div>
          <button className="profile-link" type="button" onClick={() => onOpenUserProfile(person)}>
            @{person.username}
          </button>
          <p>{fullName(person)}</p>
        </div>
      </div>
      <Badge tone="aqua">Visible neighbour</Badge>
      <p className="muted">
        Search results are returned by the social service using current neighborhood visibility and
        blocking rules.
      </p>
      <Button disabled={isSelf} loading={loading} onClick={() => onSendRequest(person.id)}>
        {isSelf ? "You cannot request yourself" : "Send request"}
      </Button>
    </Card>
  );
}
