Feature: Bulk Employee Import via CSV for OrangeHRM
  As a tester
  I want to generate a CSV file with a parametrized number of employee rows
  And import this CSV into the hs_hr_employee table
  So that I can verify bulk data insertion and integrity

  @db
  @db_csv_import
  Scenario Outline: DB-CSV-1 Generate and import CSV with <rowCount> employees
    Given I generate a CSV file "<csvFile>" with <rowCount> employees using id method "<idMethod>", first name method "<firstNameMethod>", and last name method "<lastNameMethod>"
    When I import the CSV file "<csvFile>" into the hs_hr_employee table
    Then I verify <rowCount> employees exist in the database for the imported ids, first names, and last names
    And I delete the imported employees from the hs_hr_employee table

    Examples:
      | rowCount | csvFile        | idMethod       | firstNameMethod | lastNameMethod   |
      | 5        | employees.csv  | employeeId     | employeeName    | employeeSurname  |
