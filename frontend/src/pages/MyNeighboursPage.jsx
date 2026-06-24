import { NeighbourConnections } from "../components/neighbours/NeighbourConnections";

export function MyNeighboursPage({ currentUser, neighbours, onBlock, onRemove }) {
  return (
    <NeighbourConnections
      currentUser={currentUser}
      knownUsers={neighbours.knownUsers}
      loading={neighbours.loading}
      neighbors={neighbours.neighbors}
      onBlock={onBlock}
      onRemove={onRemove}
    />
  );
}
