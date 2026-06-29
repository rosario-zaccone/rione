import { BlockedUserCard } from "./BlockedUserCard";
import { EmptyState } from "../ui/EmptyState";

export function BlockedUsersList({ blocks, knownUsers, loading, onOpenUserProfile, onUnblock }) {
  return (
    <section className="page-grid">
      <div className="page-intro card">
        <p className="eyebrow">Blocking</p>
        <h1>Blocked users</h1>
        <p>
          Unblocking removes the block but does not restore previous neighbour connections or
          pending requests.
        </p>
      </div>
      {blocks.length === 0 ? (
        <EmptyState title="No blocked users">Blocks created by the current user will appear here.</EmptyState>
      ) : (
        <div className="card-list">
          {blocks.map((block) => (
            <BlockedUserCard
              block={block}
              key={block.id}
              loading={loading}
              user={block.blocked ?? knownUsers.get(block.blockedId)}
              onOpenUserProfile={onOpenUserProfile}
              onUnblock={onUnblock}
            />
          ))}
        </div>
      )}
    </section>
  );
}
