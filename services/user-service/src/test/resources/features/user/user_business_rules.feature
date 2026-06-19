Feature: User registration

  Rule: USR-BR-001 Mail addresses must be unique

    Scenario: Reject duplicate mail
      Given a registered user has mail "ada@rione.test"
      When a resident signs up with username "grace" and mail "ada@rione.test"
      Then the user operation is rejected with "Mail is already registered"

  Rule: USR-BR-002 Usernames must be unique

    Scenario: Reject duplicate username
      Given a registered user has username "ada"
      When a resident signs up with username "ada" and mail "other@rione.test"
      Then the user operation is rejected with "Username is already registered"

  Rule: USR-BR-003 User biography must be meaningful

    Scenario: Create user account with a meaningful biography
      When a resident signs up with username "grace" and mail "grace@rione.test"
      Then the user account is created
