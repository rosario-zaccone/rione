import { RequestCard } from "./RequestCard";
import { EmptyState } from "../ui/EmptyState";

export function NeighbourRequests({
  knownUsers,
  loading,
  onAccept,
  onDecline,
  received,
  sent,
  tab,
  onTabChange,
}) {
  const active = tab === "sent" ? sent : received;

  return (
    <section className="page-grid">
      <div className="page-intro card">
        <p className="eyebrow">Requests</p>
        <h1>Neighbour requests</h1>
        <p>Users can list only requests they sent or received. Invalid operations are rejected by the backend.</p>
      </div>
      <div className="tabs" role="tablist" aria-label="Neighbour request views">
        <button className={tab === "received" ? "tab active" : "tab"} onClick={() => onTabChange("received")} type="button">
          Received requests
        </button>
        <button className={tab === "sent" ? "tab active" : "tab"} onClick={() => onTabChange("sent")} type="button">
          Sent requests
        </button>
      </div>
      {active.length === 0 ? (
        <EmptyState title={tab === "sent" ? "No sent requests" : "No received requests"}>
          Requests from the backend will appear here.
        </EmptyState>
      ) : (
        <div className="card-list">
          {active.map((request) => {
            const userId = tab === "sent" ? request.receiverId : request.senderId;
            return (
              <RequestCard
                key={request.id}
                knownUsers={knownUsers}
                loading={loading}
                mode={tab}
                request={request}
                user={knownUsers.get(userId)}
                onAccept={onAccept}
                onDecline={onDecline}
              />
            );
          })}
        </div>
      )}
    </section>
  );
}
