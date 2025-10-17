Feature:Dynamic Database CRUD Operations for OrangeHRM
  As a tester
  I want to perform CRUD operations on the hs_hr_employee table with dynamically generated data
  So that I can verify data integrity using method-based generators backed by regex properties

  @db
  @db_dynamic
  Scenario Outline: DB-1 Perform CRUD operation with method-based dynamic data
    Given I connect to the database
    When I insert an employee with SQL "<createSql>" using id method "<idMethod>" and name method "<nameMethod>"
    Then I verify employee exists with SQL "<readSql>" for generated id and name
    When I update the employee with SQL "<updateSql>" using name method "<updatedNameMethod>"
    Then I verify updated employee exists with SQL "<readSql>" for generated id and updated name
    When I delete the employee with SQL "<deleteSql>"
    Then I verify employee does not exist with SQL "<readSql>"

    Examples:
      | idMethod       | nameMethod          | updatedNameMethod       | createSql                                                               | readSql                                                        | updateSql                                                         | deleteSql                                        |
      | employeeId     | employeeName        | updatedEmployeeName     | INSERT INTO hs_hr_employee (employee_id, emp_firstname) VALUES (?, ?)   | SELECT emp_firstname FROM hs_hr_employee WHERE employee_id = ? | UPDATE hs_hr_employee SET emp_firstname = ? WHERE employee_id = ? | DELETE FROM hs_hr_employee WHERE employee_id = ? |
      | employeeIdAlt  | employeeNameAlt     | updatedEmployeeNameAlt  | INSERT INTO hs_hr_employee (employee_id, emp_firstname) VALUES (?, ?)   | SELECT emp_firstname FROM hs_hr_employee WHERE employee_id = ? | UPDATE hs_hr_employee SET emp_firstname = ? WHERE employee_id = ? | DELETE FROM hs_hr_employee WHERE employee_id = ? |
