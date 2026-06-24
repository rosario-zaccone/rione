Feature: Notification service business rules

  Rule: NOT-BR-001 Request received events notify the receiver

    Scenario: Create a notification for the request receiver
      When a request received event says user 1 sent request 10 to user 2
      Then user 2 has a "REQUEST_RECEIVED" notification from user 1

  Rule: NOT-BR-002 Request accepted events notify the original sender

    Scenario: Create a notification for the original request sender
      When a request accepted event says user 2 accepted request 10 from user 1
      Then user 1 has a "REQUEST_ACCEPTED" notification from user 2
