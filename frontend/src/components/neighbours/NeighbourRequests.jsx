import { RequestCard } from "./RequestCard";
import { EmptyState } from "../ui/EmptyState";

export function NeighbourRequests({
  knownUsers,
  loading,
  onAccept,
  onDecline,
  onOpenUserProfile,
  received,
  sent,
  tab,
  onTabChange,
}) {
  const active = tab === "sent" ? sent : received;

  return (
    <div className="page-grid">
      <div className="tabs" role="tablist" aria-label="Visualizzazioni richieste di vicinato">
        <button className={tab === "received" ? "tab active" : "tab"} onClick={() => onTabChange("received")} type="button">
          Richieste ricevute
        </button>
        <button className={tab === "sent" ? "tab active" : "tab"} onClick={() => onTabChange("sent")} type="button">
          Richieste inviate
        </button>
      </div>
      {active.length === 0 ? (
        <EmptyState title={tab === "sent" ? "Nessuna richiesta inviata" : "Nessuna richiesta ricevuta"}>
          Le tue richieste di vicinato appariranno qui.
        </EmptyState>
      ) : (
        <div className="card-list">
          {active.map((request) => {
            const userId = tab === "sent" ? request.receiverId : request.senderId;
            const profile = tab === "sent" ? request.receiver : request.sender;
            return (
              <RequestCard
                key={request.id}
                knownUsers={knownUsers}
                loading={loading}
                mode={tab}
                request={request}
                user={profile ?? knownUsers.get(userId)}
                onAccept={onAccept}
                onDecline={onDecline}
                onOpenUserProfile={onOpenUserProfile}
              />
            );
          })}
        </div>
      )}
    </div>
  );
}
