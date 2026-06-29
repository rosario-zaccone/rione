import { Avatar } from "../ui/Avatar";
import { Button } from "../ui/Button";

export function UserProfileModal({ neighborhoodLabel, onClose, user }) {
  if (!user) {
    return null;
  }

  return (
    <div className="modal-backdrop" role="presentation">
      <section className="modal-card user-profile-modal" aria-modal="true" role="dialog" aria-labelledby="profile-modal-title">
        <div className="profile-modal-heading">
          <Avatar user={user} size="lg" />
          <div>
            <p className="eyebrow">Profilo utente</p>
            <h2 id="profile-modal-title">
              {user.name} {user.surname}
            </h2>
            <p>@{user.username}</p>
          </div>
        </div>
        {user.neighborhoodId ? (
          <p className="muted">{neighborhoodLabel(user.neighborhoodId)}</p>
        ) : null}
        {user.bio ? <p>{user.bio}</p> : null}
        <div className="button-row end">
          <Button variant="ghost" onClick={onClose}>
            Close
          </Button>
        </div>
      </section>
    </div>
  );
}
