import { fullName, username } from "./personUtils";
import { Avatar } from "../ui/Avatar";
import { Button } from "../ui/Button";
import { Card } from "../ui/Card";

export function BlockedUserCard({ block, loading, onUnblock, user }) {
  return (
    <Card className="request-card" as="article">
      <Avatar user={user} />
      <div>
        <h3>{fullName(user, block.blockedId)}</h3>
        <p>{username(user, block.blockedId)}</p>
        <small>Only blocks created by you are listed.</small>
      </div>
      <Button variant="ghost" loading={loading} onClick={() => onUnblock(block.blockedId)}>
        Unblock
      </Button>
    </Card>
  );
}
