import { NeighbourCard } from "./NeighbourCard";
import { EmptyState } from "../ui/EmptyState";

export function NeighbourConnections({ currentUser, knownUsers, loading, neighbors, onBlock, onRemove }) {
  return (
    <section className="page-grid">
      <div className="page-intro card">
        <p className="eyebrow">My Neighbours</p>
        <h1>Accepted neighbour connections</h1>
        <p>Removing a neighbour does not imply deleting historical activity. Blocking takes precedence.</p>
      </div>
      {neighbors.length === 0 ? (
        <EmptyState title="No neighbours yet">Accepted backend connections will appear here.</EmptyState>
      ) : (
        <div className="people-grid">
          {neighbors.map((neighbor) => {
            const counterpartId = neighbor.userId === currentUser?.id ? neighbor.neighborId : neighbor.userId;
            return (
              <NeighbourCard
                currentUser={currentUser}
                key={neighbor.id}
                loading={loading}
                neighbor={neighbor}
                user={knownUsers.get(counterpartId)}
                onBlock={onBlock}
                onRemove={onRemove}
              />
            );
          })}
        </div>
      )}
    </section>
  );
}
