import { NeighbourRequests } from "../components/neighbours/NeighbourRequests";

export function RequestsPage({
  knownUsers,
  neighbours,
  onAccept,
  onDecline,
  onOpenUserProfile,
  onTabChange,
  tab,
}) {
  return (
    <NeighbourRequests
      knownUsers={knownUsers}
      loading={neighbours.loading}
      received={neighbours.receivedRequests}
      sent={neighbours.sentRequests}
      tab={tab}
      onAccept={onAccept}
      onDecline={onDecline}
      onOpenUserProfile={onOpenUserProfile}
      onTabChange={onTabChange}
    />
  );
}
