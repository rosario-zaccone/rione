import { Avatar } from "../ui/Avatar";
import { Card } from "../ui/Card";

export function ProfileCard({ neighborhoodLabel, user }) {
  return (
    <Card className="profile-card">
      <Avatar user={user} size="lg" />
      <div>
        <p className="eyebrow">Profilo</p>
        <h1>
          {user?.name} {user?.surname}
        </h1>
        <p className="muted">
          @{user?.username} / {neighborhoodLabel(user?.neighborhoodId)}
        </p>
      </div>
    </Card>
  );
}
