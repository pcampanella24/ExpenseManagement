# Expense Management

## Overview
This project implements a basic expense management system that allows users to:
- Create new expenses
- Retrieve all expenses
- Delete expenses

The application uses Spring Boot for the backend API, Spring Data JPA for database operations, and an H2 in-memory database for data storage.

## Technical Stack
- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **H2 Database**
- **Maven** for dependency management
- **JUnit 5** and **MockMVC** for testing

## Testing
I've implemented a comprehensive test suite covering both unit and integration tests:
- Unit tests for service layer with JUnit 5
- API integration tests using MockMVC

### Testing Configuration
By default, the test profile is commented out in `ExpenseManagementApplicationTests.java`. To enable it, remove the comment from the `@ActiveProfiles("test")` annotation:

@SpringBootTest
//@ActiveProfiles("test")
class ExpenseManagementApplicationTests {

should be changed to:

@SpringBootTest
@ActiveProfiles("test")
class ExpenseManagementApplicationTests {

This ensures that the application runs with the `test` profile, using an in-memory H2 database for testing.  

## Setup and Running the Application
- JDK 17 or higher
- Maven 3.6 or higher

### Environment Configuration
The application uses a .env file to manage credentials and configuration settings. Create a .env file in the project root with the following variables:

DB_URL=jdbc:h2:mem:expensedb
DB_CLASS=org.h2.Driver
DB_USER=adminuser
DB_PASSWORD=

These environment variables are passed to the application through application.properties.

### Installation
1. Clone the repository:
git clone https://github.com/pcampanella24/ExpenseManagement.git

The API will be available at http://localhost:8090

## Frontend
A simple HTML/JavaScript frontend is included in the `/src/main/resources/static/frontend` directory. It provides a basic user interface to interact with the API.

To access the frontend, navigate to http://localhost:8090 after starting the application.
