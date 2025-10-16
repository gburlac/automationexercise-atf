Feature: Database CRUD Operations for OrangeHRM
    As a tester
    I want to perform CRUD operations on the hs_hr_employee table
    So that I can verify data integrity and correctness

  @db
  Scenario Outline: DB-1 Perform CRUD operation in the hs_hr_employee table using SQL from feature
    Given I connect to the database
    When I insert an employee with SQL "<createSql>" and id "<id>" and name "<name>"
    Then I verify employee exists with SQL "<readSql>" and id "<id>" and name "<name>"
    When I update the employee with SQL "<updateSql>" and name "<updatedName>" and id "<id>"
    Then I verify employee exists with SQL "<readSql>" and id "<id>" and name "<updatedName>"
    When I delete the employee with SQL "<deleteSql>" and id "<id>"
    Then I verify employee does not exist with SQL "<readSql>" and id "<id>"

    Examples:
      | id  | name   | updatedName | createSql                                                               | readSql                                                        | updateSql                                                         | deleteSql                                        |
      | 103 | George | Georgina    | INSERT INTO hs_hr_employee (employee_id, emp_firstname) VALUES (?, ?)   | SELECT emp_firstname FROM hs_hr_employee WHERE employee_id = ? | UPDATE hs_hr_employee SET emp_firstname = ? WHERE employee_id = ? | DELETE FROM hs_hr_employee WHERE employee_id = ? |
      | 104 | Alina  | Alin        | INSERT INTO hs_hr_employee (employee_id, emp_firstname) VALUES (?, ?)   | SELECT emp_firstname FROM hs_hr_employee WHERE employee_id = ? | UPDATE hs_hr_employee SET emp_firstname = ? WHERE employee_id = ? | DELETE FROM hs_hr_employee WHERE employee_id = ? |
      | 105 | Rita   | Margarita   | INSERT INTO hs_hr_employee (employee_id, emp_firstname) VALUES (?, ?)   | SELECT emp_firstname FROM hs_hr_employee WHERE employee_id = ? | UPDATE hs_hr_employee SET emp_firstname = ? WHERE employee_id = ? | DELETE FROM hs_hr_employee WHERE employee_id = ? |
