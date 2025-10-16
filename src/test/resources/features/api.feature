Feature: API tests for AutomationExercise
  @api
  @api-get
  Scenario: API-1 GET - Retrieve product list
    Given the AutomationExercise API is available
    When I send a GET request to "productsList"
    Then the response status should be 200
    And the response should contain "products"
  @api
  @api-post
  Scenario: API-2 POST - Verify login with invalid credentials
    Given the AutomationExercise API is available
    When I send a POST request to "verifyLogin" with email "fake@example.com" and password "wrongpass"
    Then the response status should be 200
    And the response should contain "User not found!"
