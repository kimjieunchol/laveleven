# AI-Label-Service Backend Architecture

This document outlines the architecture and folder structure of the AI-Label-Service backend.

## 1. Project Overview

The backend is a Spring Boot application responsible for:
- User authentication and management.
- Handling the multi-step label processing pipeline (SCAN -> SCHEMA -> TRANSLATE -> SKETCH).
- Interacting with external AI/ML Python services for OCR, data structuring, translation, and validation.
- Persisting all data related to users, items, and processing steps into a PostgreSQL database.
- Providing a RESTful API for the frontend client.

## 2. Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Data Persistence**: Spring Data JPA (Hibernate)
- **Database**: PostgreSQL
- **Security**: Spring Security with JWT (JSON Web Tokens)
- **API Communication**:
    - `WebClient` (for asynchronous calls to the main Python pipeline service)
    - `RestTemplate` (for synchronous calls to other Python services)
- **Resilience**: Resilience4j (Circuit Breaker, Retry, Bulkhead patterns)
- **Build Tool**: Maven

## 3. Folder Structure (`/backend`)

```
/backend
├── pom.xml                 # Maven project configuration, dependencies.
├── README.md               # Project setup and API endpoint information.
├── /uploads/               # Directory for storing uploaded label images.
└── /src/main/
    ├── java/com/labelai/
    │   ├── LabelAiApplication.java # Main Spring Boot application entry point.
    │   │
    │   ├── /api/                 # Clients for communicating with external Python services.
    │   │   ├── FoodLabelApiClient.java # Main client for the FastAPI pipeline (OCR, Structure, Translate).
    │   │   ├── LlmApiClient.java     # Client for a separate LLM service.
    │   │   ├── RagApiClient.java     # Client for a separate RAG service.
    │   │   └── /dto/               # Data Transfer Objects for external API communication.
    │   │
    │   ├── /config/              # Application configuration classes.
    │   │   ├── SecurityConfig.java   # Spring Security, JWT filter, and URL authorization rules.
    │   │   ├── WebClientConfig.java  # Configuration for WebClient and Resilience4j patterns.
    │   │   ├── CorsConfig.java       # Cross-Origin Resource Sharing (CORS) settings.
    │   │   ├── DataInitializer.java  # Creates a default admin user on startup.
    │   │   └── ...                 # Other configurations (RestTemplate, Jackson).
    │   │
    │   ├── /controller/          # Spring MVC controllers that define the REST API endpoints.
    │   │   ├── AuthController.java     # Endpoints for login, logout, forgot password/ID.
    │   │   ├── LabelController.java    # Core endpoints for managing the label processing pipeline.
    │   │   ├── UserController.java     # Endpoints for user profile and history.
    │   │   ├── AdminController.java    # Admin-only endpoints for user and history management.
    │   │   └── HistoryController.java  # Endpoints for retrieving detailed item history and audit logs.
    │   │
    │   ├── /dto/                 # Data Transfer Objects for internal API requests/responses.
    │   │   ├── /request/           # DTOs for incoming API requests (e.g., LoginRequest).
    │   │   └── /response/          # DTOs for outgoing API responses (e.g., UserResponse).
    │   │
    │   ├── /entity/              # JPA entity classes that map to database tables.
    │   │   ├── User.java           # `users` table.
    │   │   ├── Item.java           # `items` table (the master record for a label).
    │   │   ├── Scan.java           # `scan` table (stores OCR results).
    │   │   ├── Schema.java         # `schema` table (stores structured data).
    │   │   ├── Translate.java      # `translate` table (stores translation results).
    │   │   ├── Sketch.java         # `sketch` table (stores final HTML layout).
    │   │   └── History.java        # `history` table (audit trail for all changes).
    │   │
    │   ├── /exception/           # Global exception handling.
    │   │   ├── GlobalExceptionHandler.java # Catches exceptions and returns standardized error responses.
    │   │   └── CustomException.java      # Custom exception class for specific error scenarios.
    │   │
    │   ├── /repository/          # Spring Data JPA repositories for database access.
    │   │   └── (e.g., UserRepository.java, ItemRepository.java, ...)
    │   │
    │   ├── /security/            # Classes related to Spring Security and JWT.
    │   │   ├── JwtUtil.java          # Utility for creating and validating JWTs.
    │   │   ├── JwtAuthenticationFilter.java # Filter that intercepts requests to validate the JWT.
    │   │   └── CustomUserDetailsService.java # Loads user-specific data for Spring Security.
    │   │
    │   ├── /service/             # Business logic layer.
    │   │   ├── AuthService.java      # Logic for authentication.
    │   │   ├── LabelService.java     # Core logic for the label processing pipeline.
    │   │   ├── AdminService.java     # Logic for administrative functions.
    │   │   ├── UserService.java      # Logic for user-related operations.
    │   │   └── HistoryService.java   # Logic for managing and retrieving history/audit data.
    │   │
    │   └── /util/                # General utility classes.
    │       ├── FileUtil.java       # Utilities for handling file uploads and deletions.
    │       └── DateUtil.java       # Date and time formatting utilities.
    │
    └── resources/
        └── application.yml       # Main application configuration file (database, JWT, API URLs, etc.).
```

## 4. Core Workflow: Label Processing Pipeline

The primary function of the backend is to orchestrate the label processing pipeline. This is not a single, monolithic API call but a series of interactions coordinated by the frontend and backend.

1.  **Start Pipeline (`POST /api/label/start-pipeline`)**:
    - The frontend sends an initial request with the item's name (e.g., file name).
    - The backend creates a new `Item` entity in the database, generating a unique `itemId`.
    - This `itemId` is returned to the frontend and used to track all subsequent steps for this specific label.

2.  **Individual Steps (e.g., `POST /api/label/log-ocr-history/{itemId}`)**:
    - The frontend calls specific endpoints for each step (OCR, Structure, Translate).
    - The backend receives the request, forwards it to the appropriate external Python service (via `FoodLabelApiClient`), and receives the result.
    - **Crucially**, at these intermediate stages, the results are **not** saved to their respective tables (`scan`, `schema`, `translate`). They are returned directly to the frontend for the user to review and potentially edit.
    - A record is added to the `history` table to log that the step was performed.

3.  **Generate HTML (`POST /api/label/transient/generate-html`)**:
    - After translation, the frontend can request an HTML preview of the final label.
    - The backend calls the Python service to generate the HTML and returns it directly as a string. This is a "transient" call and does not save anything to the database.

4.  **Save Full Pipeline (`POST /api/label/pipeline/{itemId}`)**:
    - Once the user is satisfied with all the steps and the final preview, the frontend makes this final call.
    - The request payload contains the final, user-approved data for every step (OCR, Structure, Translate, and the final HTML Sketch).
    - The backend receives this complete dataset and saves each part to its corresponding database table (`scan`, `schema`, `translate`, `sketch`).
    - A final `history` record is created for the `SKETCH` (save) action.

This architecture ensures that the user has full control over the process, allowing for review and modification at each step before the final data is permanently stored.
