import { BlockedUserCard } from "./BlockedUserCard";
import { EmptyState } from "../ui/EmptyState";

export function BlockedUsersList({ blocks, knownUsers, loading, onOpenUserProfile, onUnblock }) {
  return (
    <section className="page-grid">
      <div className="page-intro card">
        <p className="eyebrow">Blocco</p>
        <h1>Utenti bloccati</h1>
        <p>
          Sbloccare rimuove il blocco ma non ripristina le precedenti connessioni con i vicini o le
          richieste in sospeso.
        </p>
      </div>
      {blocks.length === 0 ? (
        <EmptyState title="Nessun utente bloccato">I blocchi creati dall'utente corrente appariranno qui.</EmptyState>
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
