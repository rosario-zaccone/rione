import { BlockedUsersList } from "../components/neighbours/BlockedUsersList";

export function BlockedUsersPage({ neighbours, onUnblock }) {
  return (
    <BlockedUsersList
      blocks={neighbours.blocks}
      knownUsers={neighbours.knownUsers}
      loading={neighbours.loading}
      onUnblock={onUnblock}
    />
  );
}
