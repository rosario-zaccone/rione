import { BlockedUsersList } from "../components/neighbours/BlockedUsersList";

export function BlockedUsersPage({ knownUsers, neighbours, onOpenUserProfile, onUnblock }) {
  return (
    <BlockedUsersList
      blocks={neighbours.blocks}
      knownUsers={knownUsers}
      loading={neighbours.loading}
      onOpenUserProfile={onOpenUserProfile}
      onUnblock={onUnblock}
    />
  );
}
