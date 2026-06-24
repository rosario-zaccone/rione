import { NeighbourSearch } from "../components/neighbours/NeighbourSearch";

export function FindNeighboursPage({
  currentUser,
  neighbours,
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
      onQueryChange={onQueryChange}
      onSendRequest={onSendRequest}
    />
  );
}
