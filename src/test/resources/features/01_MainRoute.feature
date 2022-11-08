Feature: Main Route

  Check if main-route can consume

  As a user
  I want to see the route consumes the defined endpoint

  Background:
    Given payload file "test-payload.json" is set
	Given environment for 'main-route' up and running

  Scenario: Check for consumed main route data
	When data received from endpoint
    Then consumed endpoint contains the following data
      | name    | age  |
      | Michael | 55   |
      | Sylvia  | 35   |

