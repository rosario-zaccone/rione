import { fullName, username } from "./personUtils";
import { Avatar } from "../ui/Avatar";
import { Button } from "../ui/Button";
import { Card } from "../ui/Card";

export function BlockedUserCard({ block, loading, onOpenUserProfile, onUnblock, user }) {
  return (
    <Card className="request-card" as="article">
      <Avatar user={user} />
      <div>
        {user ? (
          <button className="profile-link" type="button" onClick={() => onOpenUserProfile(user)}>
            @{user.username}
          </button>
        ) : (
          <h3>{fullName(user, block.blockedId)}</h3>
        )}
        <p>{user ? fullName(user) : username(user, block.blockedId)}</p>
        <small>Sono elencati solo i blocchi creati da te.</small>
      </div>
      <Button variant="ghost" loading={loading} onClick={() => onUnblock(block.blockedId)}>
        Sblocca
      </Button>
    </Card>
  );
}
