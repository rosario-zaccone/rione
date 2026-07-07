import { NeighbourCard } from "./NeighbourCard";
import { EmptyState } from "../ui/EmptyState";

export function NeighbourConnections({
  currentUser,
  knownUsers,
  loading,
  neighbors,
  onBlock,
  onOpenUserProfile,
  onRemove,
}) {
  return (
    <div className="page-grid">
      {neighbors.length === 0 ? (
        <EmptyState title="Ancora nessun vicino">Le connessioni accettate appariranno qui.</EmptyState>
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
                user={neighbor.neighbor ?? knownUsers.get(counterpartId)}
                onBlock={onBlock}
                onOpenUserProfile={onOpenUserProfile}
                onRemove={onRemove}
              />
            );
          })}
        </div>
      )}
    </div>
  );
}
