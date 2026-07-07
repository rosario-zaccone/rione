import { NeighbourSearchResultCard } from "./NeighbourSearchResultCard";
import { EmptyState } from "../ui/EmptyState";
import { SearchInput } from "../ui/SearchInput";
import { Skeleton } from "../ui/Skeleton";
import { Toast } from "../ui/Toast";

export function NeighbourSearch({
  currentUser,
  error,
  loading,
  onDismissError,
  onOpenUserProfile,
  onQueryChange,
  onSendRequest,
  query,
  results,
  searchLoading,
}) {
  return (
    <section className="page-grid">
      <div className="page-intro card">
        <p className="eyebrow">Trova vicini</p>
        <h1>Scopri le persone visibili nel tuo quartiere</h1>
        <p>
          Cerca per username, nome o cognome e invia una richiesta di connessione.
        </p>
        <SearchInput
          label="Cerca vicini visibili"
          placeholder="Digita un nome o uno username"
          value={query}
          onChange={onQueryChange}
        />
      </div>
      <Toast message={error} tone="error" onClose={onDismissError} />
      {!query.trim() ? (
        <EmptyState title="Inizia una ricerca">Cerca i vicini visibili nel tuo quartiere attuale.</EmptyState>
      ) : searchLoading ? (
        <Skeleton lines={4} />
      ) : results.length === 0 ? (
        <EmptyState title="Nessun vicino visibile">Nessun vicino corrisponde a questa ricerca.</EmptyState>
      ) : (
        <div className="people-grid">
          {results.map((person) => (
            <NeighbourSearchResultCard
              currentUser={currentUser}
              key={person.id}
              loading={loading}
              onOpenUserProfile={onOpenUserProfile}
              person={person}
              onSendRequest={onSendRequest}
            />
          ))}
        </div>
      )}
    </section>
  );
}
