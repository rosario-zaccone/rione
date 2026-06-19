Feature: Social service business rules

  Rule: SOC-BR-001 Neighbour requests require the same neighborhood

    Scenario: Create a neighbour request between users in the same neighborhood
      Given users 1 and 2 belong to the same neighborhood
      When user 1 sends a neighbour request to user 2
      Then the neighbour request is created

    Scenario: Reject a neighbour request between users in different neighborhoods
      Given users 1 and 2 belong to different neighborhoods
      When user 1 sends a neighbour request to user 2
      Then the social operation is rejected with "Users must belong to the same neighborhood"

  Rule: SOC-BR-002 A user cannot send a neighbour request to themselves

    Scenario: Reject a self neighbour request
      When user 1 sends a neighbour request to user 1
      Then the social operation is rejected with "Neighbor request sender and receiver must be different users"

  Rule: SOC-BR-003 Only one pending neighbour request can exist between two users

    Scenario: Reject a duplicate pending neighbour request
      Given a pending neighbour request exists from user 1 to user 2
      When user 2 sends a neighbour request to user 1
      Then the social operation is rejected with "A pending neighbor request already exists between these users"

  Rule: SOC-BR-004 Accepting a neighbour request creates a reciprocal neighbour connection

    Scenario: Accept a neighbour request
      Given a pending neighbour request exists from user 1 to user 2
      When the request is accepted
      Then a reciprocal neighborship exists between user 1 and user 2

  Rule: SOC-BR-005 Blocking a neighbour does not delete historical activity

    Scenario: Blocking removes the active neighborship
      Given users 1 and 2 are neighbours
      When user 1 blocks user 2
      Then the neighborship between user 1 and user 2 is removed

  Rule: SOC-BR-006 Blocking takes precedence over social interactions

    Scenario: Reject a request from a blocked user
      Given user 1 has blocked user 2
      When user 2 sends a neighbour request to user 1
      Then the social operation is rejected with "Neighbor request cannot be sent between blocked users"

  Rule: SOC-BR-007 Unblocking removes the block but does not restore prior relationships

    Scenario: Unblock without restoring a prior neighborship
      Given user 1 has blocked user 2
      When user 1 unblocks user 2
      Then user 2 is no longer blocked by user 1
      And the neighborship between user 1 and user 2 is not restored

  Rule: SOC-BR-008 Neighborhood changes remove now-invalid relationships

    Scenario: Remove relationships outside the new neighborhood
      Given user 1 changes neighborhood
      When social relationships are reconciled for user 1
      Then neighbour connections outside the new neighborhood are removed
      And pending neighbour requests outside the new neighborhood are removed
