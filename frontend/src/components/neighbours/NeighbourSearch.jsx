import { NeighbourSearchResultCard } from "./NeighbourSearchResultCard";
import { EmptyState } from "../ui/EmptyState";
import { SearchInput } from "../ui/SearchInput";
import { Skeleton } from "../ui/Skeleton";

export function NeighbourSearch({
  currentUser,
  error,
  loading,
  onQueryChange,
  onSendRequest,
  query,
  results,
  searchLoading,
}) {
  return (
    <section className="page-grid">
      <div className="page-intro card">
        <p className="eyebrow">Find Neighbours</p>
        <h1>Discover visible people in your neighborhood</h1>
        <p>
          Search by username, name, or surname. Eligibility, duplicate pending requests, and
          blocking are enforced by the backend.
        </p>
        <SearchInput
          label="Search visible neighbours"
          placeholder="Type a name or username"
          value={query}
          onChange={onQueryChange}
        />
      </div>
      {error ? <p className="form-error">{error}</p> : null}
      {!query.trim() ? (
        <EmptyState title="Start a search">Search visible neighbours in your current neighborhood.</EmptyState>
      ) : searchLoading ? (
        <Skeleton lines={4} />
      ) : results.length === 0 ? (
        <EmptyState title="No visible neighbours">No backend results matched this search.</EmptyState>
      ) : (
        <div className="people-grid">
          {results.map((person) => (
            <NeighbourSearchResultCard
              currentUser={currentUser}
              key={person.id}
              loading={loading}
              person={person}
              onSendRequest={onSendRequest}
            />
          ))}
        </div>
      )}
    </section>
  );
}
