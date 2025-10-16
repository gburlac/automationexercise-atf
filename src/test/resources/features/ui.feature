Feature: UI tests for AutomationExercise
  As a visitor
  I want to see an error for invalid credentials
  I want to search for products

  @ui
  @login
  Scenario: UI-1 Invalid login shows error
    Given I am on the home page
    When I navigate to the login page
    And I login with email "fake@example.com" and password "badpass"
    Then I should see an invalid login error

  @ui
  @search
  Scenario Outline: UI-2 Search for products
    Given I am on the home page
    When I search for "<product>"
    Then I should see results for "<product>"

    Examples:
      | product           |
      | Men Tshirt        |
      | Sleeveless Dress  |
      | Blue Top          |
