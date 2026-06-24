import { Avatar } from "../ui/Avatar";
import { Card } from "../ui/Card";

export function RightPanel({ currentUser, metrics, neighborhoodLabel }) {
  const profileBioLength = currentUser?.bio?.trim().length ?? 0;
  const completion = [
    currentUser?.name,
    currentUser?.surname,
    currentUser?.username,
    currentUser?.birthDate,
    profileBioLength >= 20,
  ].filter(Boolean).length;

  return (
    <aside className="right-panel" aria-label="Contextual summary">
      <Card className="user-summary">
        <Avatar user={currentUser} size="lg" />
        <div>
          <h2>
            {currentUser?.name} {currentUser?.surname}
          </h2>
          <p>@{currentUser?.username}</p>
        </div>
      </Card>

      <Card>
        <p className="eyebrow">Current neighborhood</p>
        <h3>{neighborhoodLabel(currentUser?.neighborhoodId)}</h3>
        <p className="muted">Discovery, requests, and connections are scoped to this membership.</p>
      </Card>

      <Card>
        <p className="eyebrow">Today</p>
        <div className="metric-grid">
          <span>
            <strong>{metrics.received}</strong>
            <small>received</small>
          </span>
          <span>
            <strong>{metrics.sent}</strong>
            <small>sent</small>
          </span>
          <span>
            <strong>{metrics.neighbors}</strong>
            <small>neighbours</small>
          </span>
          <span>
            <strong>{metrics.unread}</strong>
            <small>unread</small>
          </span>
          <span>
            <strong>{metrics.blocks}</strong>
            <small>blocked</small>
          </span>
        </div>
      </Card>

      <Card>
        <p className="eyebrow">Profile completion</p>
        <h3>{completion}/5 essentials</h3>
        <p className="muted">A complete profile helps neighbours recognize who they are connecting with.</p>
      </Card>

      <Card className="safety-note">
        <p className="eyebrow">Safety note</p>
        <p>Blocking takes precedence over discovery, requests, connections, and notifications.</p>
      </Card>
    </aside>
  );
}
