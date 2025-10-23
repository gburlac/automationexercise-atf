# AutomationExercise ATF

## Overview
AutomationExercise ATF is an automated test framework for the AutomationExercise and OrangeHRM platforms. It supports UI, API, and database testing using Java, Cucumber, JUnit, Playwright, and Allure for reporting. The framework enables dynamic test data generation, bulk CSV import, and CRUD operations on the database.

## Project Structure
- **src/main/java/**: Main application code (minimal, mostly for test support).
- **src/test/java/**: Test code, including:
  - Step definitions (`steps/`)
  - Page objects (`pages/`)
  - Hooks and runners (`hooks/`, `runners/`)
  - Database utilities (`db/`)
  - Support classes (`support/`)
- **src/test/resources/features/**: Cucumber feature files for UI, API, and DB scenarios.
- **target/**: Test reports and build artifacts.
- **allure-results/**: Allure report results.
- **logs/**: Test execution logs.
- **employees.csv**: Example CSV for bulk employee import.

## Key Features
- **UI Testing**: Scenarios for login and product search using Playwright.
- **API Testing**: Scenarios for GET and POST requests to AutomationExercise API.
- **Database Testing**: Dynamic CRUD operations and bulk CSV import for OrangeHRM's `hs_hr_employee` table.
- **Test Data Generation**: Uses Generex for regex-based data generation.
- **Reporting**: Allure and Cucumber reports.
- **CI/CD**: Jenkins pipeline for automated build, test, and reporting.

## How to Run
1. **Install dependencies**:  
   Run `mvn clean install` to build the project and download dependencies.
2. **Run tests**:  
   Use `mvn test` to execute all tests.
3. **Generate Allure report**:  
   After tests, run `mvn allure:report` and open the generated report in `target/allure-report`.
4. **Jenkins Pipeline**:  
   The `Jenkinsfile` defines stages for checkout, browser install, build/test, and reporting.

## Main Technologies
- **Java 17**
- **JUnit 5**
- **Cucumber 7**
- **Playwright Java**
- **Allure**
- **Generex**
- **Jenkins**

## Example Scenarios
- **Bulk Employee Import**:  
  Generate a CSV with employees, import to DB, verify, and clean up.
- **Dynamic DB CRUD**:  
  Insert, update, verify, and delete employees with generated data.
- **UI Login & Search**:  
  Validate login errors and product search results.
- **API Requests**:  
  Test product list retrieval and login error handling.

## Configuration
- **pom.xml**: Maven build and dependency management.
- **Jenkinsfile**: CI/CD pipeline configuration.

## Contributing
1. Fork the repository.
2. Create a feature branch.
3. Commit your changes.
4. Submit a pull request.

## License
Specify your license here (e.g., MIT, Apache 2.0).

