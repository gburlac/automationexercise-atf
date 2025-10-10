Feature: Login
  As a visitor
  I want to see an error for invalid credentials

  @login
  Scenario: Invalid login shows error
    Given I am on the home page
    When I navigate to the login page
    And I login with email "fake@example.com" and password "badpass"
    Then I should see an invalid login error
    #And I take a screenshot