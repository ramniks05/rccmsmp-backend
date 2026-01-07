# RCCMS Manipur Backend

Spring Boot backend application for RCCMS (Manipur) system.

## Technology Stack

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Build Tool**: Maven
- **Database**: PostgreSQL (Production) / H2 (Development)
- **Security**: Spring Security (Basic configuration, ready for JWT)

## Project Structure

```
src/main/java/in/gov/manipur/rccms/
├── config/              # Configuration classes (CORS, etc.)
├── security/            # Spring Security configuration
├── controller/          # REST Controllers
├── service/             # Business logic layer
├── repository/          # Data access layer (JPA repositories)
├── entity/              # JPA entities
├── dto/                 # Data Transfer Objects
└── exception/           # Exception handlers
```

## Features

- ✅ Layered architecture (Controller → Service → Repository)
- ✅ JPA with audit fields (createdAt, updatedAt)
- ✅ Global exception handling
- ✅ Standardized API response format
- ✅ CORS enabled for Angular frontend
- ✅ Spring Security (basic setup, ready for JWT)
- ✅ Health check endpoint
- ✅ Profile-based configuration (dev/prod)

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL (for production) or H2 (for development)

### Running the Application

1. **Development Mode (H2 Database)**:
   ```bash
   mvn spring-boot:run
   ```
   Or set active profile:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

2. **Production Mode (PostgreSQL)**:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=prod
   ```
   Make sure PostgreSQL is running and update database credentials in `application.yml`

### API Endpoints

- **Health Check**: `GET http://localhost:8080/api/health`
- **H2 Console** (dev only): `http://localhost:8080/h2-console`

### Sample Entity Endpoints

- `GET /api/samples` - Get all samples
- `GET /api/samples/{id}` - Get sample by ID
- `POST /api/samples` - Create sample
- `PUT /api/samples/{id}` - Update sample
- `DELETE /api/samples/{id}` - Delete sample

## Configuration

### Application Profiles

- **dev**: Uses H2 in-memory database, detailed logging
- **prod**: Uses PostgreSQL, optimized logging

### Database Configuration

Update `application.yml` or use environment variables:
- `DB_USERNAME`: Database username (default: postgres)
- `DB_PASSWORD`: Database password (default: postgres)

## Security

Currently configured to permit all requests. Ready for JWT implementation:
- CORS enabled for Angular frontend
- Stateless session management
- CSRF disabled (will be enabled with JWT)

## Building the Project

```bash
mvn clean install
```

## Git Repository Setup

### Initial Setup

To initialize Git and push to GitHub, run:

```bash
# Windows
init-git.bat

# Or manually:
git init
git add .
git commit -m "Initial commit: Spring Boot project setup"
git branch -M main
git remote add origin https://github.com/ramniks05/rccmsmp-backend.git
git push -u origin main
```

### Repository

- **GitHub**: https://github.com/ramniks05/rccmsmp-backend.git

## License

Government of Manipur - RCCMS Project

