import { NeighbourRequests } from "../components/neighbours/NeighbourRequests";

export function RequestsPage({ neighbours, onAccept, onDecline, onTabChange, tab }) {
  return (
    <NeighbourRequests
      knownUsers={neighbours.knownUsers}
      loading={neighbours.loading}
      received={neighbours.receivedRequests}
      sent={neighbours.sentRequests}
      tab={tab}
      onAccept={onAccept}
      onDecline={onDecline}
      onTabChange={onTabChange}
    />
  );
}
