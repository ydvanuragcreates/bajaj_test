# Webhook Automation Application

A Spring Boot application that automates the BFHL API webhook flow. The application executes on startup, processes the required API calls, and exits automatically.

## Overview

This application implements an automated workflow that:
1. Sends an initial POST request with user credentials
2. Receives and parses webhook URL and access token
3. Determines the appropriate SQL query based on registration number parity
4. Submits the final SQL query to the webhook endpoint

## Technical Stack

- **Java**: 17+
- **Spring Boot**: 3.2.0
- **Build Tool**: Maven 3.6+
- **HTTP Client**: RestTemplate
- **JSON Processing**: Jackson

## Project Structure

```
webhook-automation/
├── src/main/
│   ├── java/com/bfhl/webhook/
│   │   ├── WebhookAutomationApplication.java
│   │   ├── model/
│   │   │   ├── InitialRequest.java
│   │   │   ├── WebhookResponse.java
│   │   │   └── FinalSubmission.java
│   │   └── runner/
│   │       └── WebhookAutomationRunner.java
│   └── resources/
│       └── application.properties
├── pom.xml
└── README.md
```

## Configuration

Edit `src/main/resources/application.properties` with your credentials:

```properties
user.name=Your_Name
user.regNo=YourRegNo
user.email=your.email@example.com

api.initial.url=https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA

sql.odd.solution=YOUR_ODD_SQL_QUERY
sql.even.solution=YOUR_EVEN_SQL_QUERY
```

### SQL Query Selection Logic

- If the last two digits of `regNo` are **odd**: uses `sql.odd.solution`
- If the last two digits of `regNo` are **even**: uses `sql.even.solution`

## Build and Run

### Using Maven

```bash
# Clean and build
mvn clean package

# Run the application
mvn spring-boot:run
```

### Using JAR

```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/webhook-automation-1.0.0.jar
```

## Expected Output

```
=== Starting Webhook Automation Flow ===

Step 1: Sending initial request...
✓ Received webhook URL and access token

Step 2: Analyzing registration number...
✓ SQL query selected

Step 3: Submitting final query...
✓ Final submission completed successfully!

=== Automation Flow Completed Successfully ===
```

## Architecture

### Components

**WebhookAutomationApplication**
- Main Spring Boot application class
- Entry point for the application

**WebhookAutomationRunner**
- Implements `ApplicationRunner` interface
- Executes the automation flow on application startup
- Handles HTTP communication with REST APIs

**Model Classes**
- `InitialRequest`: Request payload for initial API call
- `WebhookResponse`: Response structure from initial API
- `FinalSubmission`: Request payload for final webhook submission

### Workflow

```
Application Start
      ↓
Step 1: POST /generateWebhook/JAVA
      ↓
Parse Response (webhook URL + access token)
      ↓
Step 2: Determine SQL Query (odd/even logic)
      ↓
Step 3: POST to webhook URL with Authorization
      ↓
Application Exit
```

## Error Handling

The application includes comprehensive error handling:
- HTTP client errors (4xx) with detailed messages
- Server errors (5xx) with appropriate logging
- JSON parsing errors
- Configuration validation

## Dependencies

Key dependencies from `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

## Notes

- The application does not start a web server (no REST controllers)
- Execution is single-run: completes the flow and exits
- All configuration is externalized in `application.properties`
- Uses RestTemplate for synchronous HTTP communication
- Authorization header format: `Authorization: <accessToken>` (no "Bearer" prefix)

## Author

**Anurag Yadav**  
Registration No: 22BCE7590  
Email: anurag.22bce7590@vitapstudent.ac.in

## License

This project is created for the BFHL API Challenge.
