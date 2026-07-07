import { NeighbourSearch } from "../components/neighbours/NeighbourSearch";

export function FindNeighboursPage({
  currentUser,
  neighbours,
  onOpenUserProfile,
  onQueryChange,
  onSendRequest,
  query,
}) {
  return (
    <NeighbourSearch
      currentUser={currentUser}
      error={neighbours.error}
      loading={neighbours.loading}
      query={query}
      results={neighbours.searchResults}
      searchLoading={neighbours.searchLoading}
      onDismissError={() => neighbours.setError("")}
      onOpenUserProfile={onOpenUserProfile}
      onQueryChange={onQueryChange}
      onSendRequest={onSendRequest}
    />
  );
}
