
# Rule Engine with AST

The **Rule Engine** is a 3-tier application designed to evaluate user eligibility based on attributes such as age, department, income, and experience. It leverages an **Abstract Syntax Tree (AST)** to dynamically represent, combine, and modify conditional rules. This allows flexible rule definitions, making it an efficient solution for handling complex logic.

## Tech Stack
- **Backend**: Java Spring Boot
- **Database**: PostgreSQL
- **Frontend**: React with Vite
- **Testing**: Postman

---

## Installation and Setup

### Prerequisites
Before setting up the project, ensure that you have the following software installed:
- [Java Development Kit (JDK) 11+](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [PostgreSQL](https://www.postgresql.org/download/)
- [Node.js and npm](https://nodejs.org/en/) (for React + Vite frontend)
- [Maven](https://maven.apache.org/download.cgi) (for backend dependencies)

### Backend Setup

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/BalajiBathini/RuleEngine.git
   cd re-backend
   ```

2. **Configure PostgreSQL**:
   - Create a new PostgreSQL database (e.g., `rule_engine_db`).
   - Update the `application.properties` file with your PostgreSQL credentials:
     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5432/ruledb
     spring.datasource.username=<your-username>
     spring.datasource.password=<your-password>
     ```

3. **Add Dependencies**:
   Ensure the following dependencies are included in the `pom.xml` file:
   - **Spring Boot Starter Web**: For building the RESTful APIs and MVC architecture.
   - **Spring Boot Starter Data JPA**: To interact with PostgreSQL using JPA repositories.
   - **PostgreSQL Driver**: To communicate with the PostgreSQL database.
   - **Lombok**: To reduce boilerplate code by generating getters, setters, and constructors automatically.

4. **Run the Backend Application**:
   ```bash
   mvn spring-boot:run
   ```

### Frontend Setup

1. **Navigate to the Frontend Directory**:
   ```bash
   cd re-frontend
   ```

2. **Install Dependencies**:
   ```bash
   npm install
   ```

3. **Run the Frontend Application**:
   ```bash
   npm run dev
   ```

---

## API Design

### Functions Overview

1. **`create_rule(rule_string)`**: Parses a string representing a rule and generates a Node object that corresponds to the AST.
2. **`combine_rules(rules)`**: Accepts a list of rule strings and combines them into a single AST, optimizing efficiency and minimizing redundant checks.
3. **`evaluate_rule(JSON data)`**: Evaluates the combined AST against provided user attributes in a JSON dictionary, returning `True` or `False` based on eligibility.

### API Endpoints

- **Create Rule**: `POST /api/rules/create`
- **Combine Rules**: `POST /api/rules/combine`
- **Evaluate Rule**: `POST /api/rules/evaluate`

---

## Database Schema

The application uses a PostgreSQL database to store rules.

---

## Testing

You can test the API endpoints using Postman.

### Sample Postman Requests

1. **Create Rule**:
   - **URL**: `http://localhost:8080/api/rules/create`
   - **Method**: `POST`
   - **Body** (JSON):
     ```json
     {
       "rule_string": "((age > 30 AND department = 'Sales') OR (age < 25 AND department = 'Marketing')) AND (salary > 50000 OR experience > 5)"
     }
     ```

2. **Evaluate Rule**:
   - **URL**: `http://localhost:8080/api/rules/evaluate`
   - **Method**: `POST`
   - **Body** (JSON):
     ```json
     {
       "data": {
         "age": 35,
         "department": "Sales",
         "salary": 60000,
         "experience": 3
       }
     }
     ```

---

## Future Improvements
- **Rule Management UI**: Develop a more interactive frontend to manage, create, and combine rules.
- **Rule Versioning**: Implement rule versioning to track changes and rollbacks.
- **Performance Optimization**: Further optimize the rule evaluation process for large datasets.

---

## Conclusion

The Rule Engine application offers a flexible and efficient way to evaluate user eligibility based on dynamically generated rules. By utilizing **Spring Boot**, **PostgreSQL**, and **React with Vite**, the system is scalable and easy to maintain. Contributions and suggestions for improvements are welcome!

---

