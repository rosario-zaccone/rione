Feature: Post service business rules

  Rule: PST-BR-002 Posts must belong to a supported type

    Scenario: Create a discussion post
      When user 1 creates a public discussion post
      Then the post is created

  Rule: PST-BR-006 A user can have only one active reaction on a post

    Scenario: Updating an existing reaction keeps one reaction
      Given user 1 created a public discussion post
      And user 2 belongs to the same neighborhood as user 1
      When user 2 reacts with "UPVOTE"
      And user 2 reacts with "DOWNVOTE"
      Then the post has one reaction from user 2 with type "DOWNVOTE"

  Rule: PST-BR-009 A user can view public posts authored by users who belong to the same neighborhood

    Scenario: Same-neighborhood public post is visible without active neighborship
      Given user 1 created a public discussion post
      And user 2 belongs to the same neighborhood as user 1
      When user 2 views the post
      Then the post is visible

    Scenario: Same-neighborhood public post appears in the feed
      Given user 1 created a public discussion post
      And user 2 belongs to the same neighborhood as user 1
      When user 2 views their post feed
      Then the post feed contains the post

  Rule: PST-BR-011 A user can view private posts authored by another user only when both users belong to the same neighborhood and have an active neighborship

    Scenario: Private post is visible with active neighborship
      Given user 1 created a private help post
      And user 2 belongs to the same neighborhood as user 1
      And user 2 has active neighborship with user 1
      When user 2 views the post
      Then the post is visible

    Scenario: Private post is hidden without active neighborship
      Given user 1 created a private help post
      And user 2 belongs to the same neighborhood as user 1
      When user 2 views the post
      Then the post operation is rejected with "Post not found"

  Rule: PST-BR-013 Blocking hides posts and prevents interactions

    Scenario: Blocked user cannot view a public post
      Given user 1 created a public discussion post
      And user 2 is blocked with user 1
      When user 2 views the post
      Then the post operation is rejected with "Post not found"

  Rule: PST-BR-007 A user can update or remove only their own comments and reactions

    Scenario: Reject updating another user's comment
      Given user 1 created a public discussion post
      And user 2 belongs to the same neighborhood as user 1
      And user 2 commented on the post
      When user 3 updates the comment
      Then the post operation is rejected with "Only the author can update this comment"
