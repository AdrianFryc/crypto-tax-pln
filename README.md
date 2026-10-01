Crypto Tax PLN (crypto-tax-pln)

A production-ready, stateless RESTful API built with Java 21 and Spring Boot designed to automate cryptocurrency tax calculations in Polish Złoty (PLN) in compliance with Polish tax regulations.

🚀 Overview

The Crypto Tax PLN application provides a secure and scalable backend service for cryptocurrency investors to import transaction histories, fetch historical NBP (National Bank of Poland) exchange rates, calculate capital gains/losses under Polish tax rules (PIT-38), and generate consolidated tax reports.

Designed following Clean Architecture, SOLID principles, and REST API standards, this project serves as a practical showcase of modern backend engineering in Java.

🛠 Tech Stack & Tools

  * Core Platform: Java 21 (Records, Pattern Matching, Virtual Threads readiness)

  * Framework: Spring Boot 4.1 / Spring Framework

  * Security: Spring Security 6 (Stateless Session Policy, BCrypt Password Encoder)

   * Authentication: JJWT (io.jsonwebtoken 0.12.6) for stateless JWT management

   * Database & ORM: PostgreSQL, Spring Data JPA / Hibernate

   * Database Migrations: Flyway / Liquibase

   * Validation & Error Handling: Jakarta Validation (@Valid), @RestControllerAdvice

   * Testing: JUnit 5, AssertJ, Mockito, Testcontainers

   * DevOps & Infrastructure: Docker, Docker Compose, Git CLI, Maven, GitHub Actions (CI/CD)

📐 Architecture & Key Design Principles

   * Layered Architecture: Clear separation of concerns between Infrastructure (REST Controllers, DB Repositories, Security), Application (Services, Business Logic), and Domain (Models, Exceptions).

   * Stateless Security Architecture: Fully decoupled, token-based authentication eliminating server-side session overhead (SessionCreationPolicy.STATELESS).

   * Security & OWASP Compliance:

        Prevention of User Enumeration attacks by standardizing credential failure responses (401 Unauthorized).

        Password hashing enforced via strong BCrypt cost factors.

        CSRF disabled intentionally due to stateless header-based JWT authentication (Authorization: Bearer <token>).

   * Domain Exception Translation: Centralized error management translating domain/validation exceptions into RFC-7807 compliant HTTP responses via GlobalExceptionHandler.

📊 Project Status & Roadmap
✅ Completed Features

    [x] Authentication & User Management Module:

        Registration endpoint (POST /api/v1/auth/register) with automatic BCrypt hashing.

        Secure authentication endpoint (POST /api/v1/auth/login) issuing JWT tokens.

    [x] Security Infrastructure:

        JwtService implementing HMAC-SHA signing, claim extraction, and expiration validation.

        Centralized SecurityConfig configuring public endpoint matchers and stateless policy.

    [x] Integration & Exception Handling:

        GlobalExceptionHandler mapping validation errors, duplicate user exceptions, and invalid credentials.

        Initial NBP HTTP Client implementation for fetching PLN exchange rates.

🚧 Roadmap (In Progress / To Be Implemented)

    [ ] JWT Authentication Filter: OncePerRequestFilter implementation for automatic token extraction and SecurityContextHolder authorization on protected routes.

    [ ] Test Automation Suite:

        [x] Unit test coverage for services using JUnit 5 and Mockito.

        Integration tests with real PostgreSQL containers via Testcontainers.

    [ ] Crypto Transaction Engine:

        Manual and CSV-based transaction import APIs.

        FIFO (First-In, First-Out) tax engine converting foreign currency transactions into PLN using historical NBP rates.

    [ ] PIT-38 Tax Reporting: Automated generation of tax summaries for Polish tax filings.

    [ ] DevOps & CI/CD:

        docker-compose.yml for zero-configuration local development.

        GitHub Actions workflow for automated build, linting, and test execution on Pull Requests.

⚡ Getting Started
Prerequisites

   * JDK 21 or higher

   * Apache Maven 3.9+

   * Docker & Docker Compose (for PostgreSQL)

Running Locally

1. Clone the repository:
    git clone https://github.com/AdrianFryc/crypto-tax-pln.git
    cd crypto-tax-pln

2. Start PostgreSQL database via Docker:
   docker run --name crypto-tax-db -e POSTGRES_DB=cryptotax -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:16-alpine

3. Configure Environment Variables / Properties:
   Ensure src/main/resources/application.properties contains your local database and JWT configuration:
    jwt.secret=YourUltraSecretSigningKeyThatIsAtLeast256BitsLongForHmacSha
    jwt.expiration-ms=86400000
   
5. Build and run the application:
   mvn spring-boot:run


   🧪 API Endpoints 
   * POST(Public access): /api/v1/auth/register - Registers a new user account
   * POST(Public access): /api/v1/auth/login Authenticates credentials and returns a Bearer JWT token
   * GET(Protected access): /api/v1/transactions - (Planned) Fetches user transaction history
   * POST(Protected access): /api/v1/tax/calculate - (Planned) Triggers tax calculation for a selected tax year
