# EMR Backend System

A comprehensive Spring Boot-based Electronic Medical Records (EMR) backend system with Keycloak authentication, multi-facility patient management, and healthcare service integration.

## 🏥 Features

### Core Functionality
- **Multi-Facility Architecture**: Centralized patient management across multiple healthcare facilities
- **Patient Registration**: Complete CRUD operations with demographics, insurance, and medical services
- **Keycloak Integration**: Secure authentication and authorization with role-based access control
- **Service Management**: Support for multiple healthcare services (Lab, Radiology, Consultation)
- **Advanced Search**: Search patients by name, MRN, email, phone number with filtering capabilities
- **RESTful API**: Comprehensive REST endpoints for frontend integration

### Security Features
- **JWT Authentication**: Secure token-based authentication via Keycloak
- **Role-based Access Control**: ADMIN, FACILITY_MANAGER, STAFF roles
- **CORS Configuration**: Secure cross-origin resource sharing
- **Input Validation**: Comprehensive request validation and sanitization

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- Docker & Docker Compose
- Git

### 1. Clone the Repository

```bash
git clone https://github.com/AhmedARmohamed/emr_backend_system.git
cd emr_backend_system
```

### 2. Start Infrastructure Services

```bash
# Start PostgreSQL and Keycloak
docker-compose up -d
```

This will start:
- **PostgreSQL** on port 5432
- **Keycloak** on port 8080

### 3. Configure Keycloak

1. **Access Keycloak Admin Console:**
    - URL: http://localhost:8080
    - Username: `admin`
    - Password: `admin`

2. **Create EMR Realm:**
    - Click "Create Realm"
    - Name: `emr-realm`
    - Click "Create"

3. **Create Client:**
    - Go to Clients → Create Client
    - Client ID: `emr-backend`
    - Client Type: `OpenID Connect`
    - Click "Next"
    - Client authentication: `ON`
    - Authorization: `ON`
    - Valid redirect URIs: `http://localhost:3000/*`
    - Web origins: `http://localhost:3000`
    - Click "Save"

4. **Create Roles:**
    - Go to Realm roles → Create role
    - Create these roles:
        - `ADMIN`
        - `FACILITY_MANAGER`
        - `STAFF`

5. **Create Test Users:**
   ```bash
   # Admin User
   Username: admin
   Email: admin@emr.com
   Password: admin123
   Roles: ADMIN
   
   # Facility Manager
   Username: manager
   Email: manager@emr.com
   Password: manager123
   Roles: FACILITY_MANAGER
   
   # Staff User
   Username: staff
   Email: staff@emr.com
   Password: staff123
   Roles: STAFF
   ```

### 4. Configure Application

Update `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/emr_db
    username: emr_user
    password: emr_password
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect

  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/realms/emr-realm
          jwk-set-uri: http://localhost:8080/realms/emr-realm/protocol/openid-connect/certs

server:
  port: 8080
  servlet:
    context-path: /api

# CORS Configuration
cors:
  allowed-origins: http://localhost:3000
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS
  allowed-headers: "*"
  allow-credentials: true

# Logging
logging:
  level:
    com.emr: DEBUG
    org.springframework.security: DEBUG
```

### 5. Run the Application

```bash
# Build and run
mvn clean install
mvn spring-boot:run

# Or run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The API will be available at: http://localhost:8080/api

## 🧪 Testing

### Unit Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=PatientControllerTest

# Run tests with coverage
mvn test jacoco:report
```

### Integration Tests with Keycloak

```bash
# Start test environment
docker-compose -f docker-compose.test.yml up -d

# Run integration tests
mvn test -Dtest="**/*IntegrationTest"

# Stop test environment
docker-compose -f docker-compose.test.yml down
```

### Test Configuration

The project includes comprehensive testing setup:
- **Testcontainers**: For integration testing with real databases
- **MockMvc**: For controller testing
- **Keycloak Test Configuration**: Mock authentication for unit tests
- **H2 Database**: In-memory database for fast unit tests

## 📚 API Documentation

### Authentication
All endpoints require JWT authentication except health checks.

**Headers Required:**
```
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

### Patient Management

#### Create Patient
```http
POST /api/patients
Content-Type: application/json

{
  "mrn": "MRN-2024-001",
  "firstName": "John",
  "lastName": "Doe",
  "gender": "MALE",
  "dateOfBirth": "1990-01-15",
  "phoneNumber": "+1234567890",
  "email": "john.doe@email.com",
  "address": "123 Main St, City, State 12345",
  "insuranceProvider": "Blue Cross",
  "insurancePolicyNumber": "BC123456789",
  "facilityId": "facility-uuid",
  "services": ["service-uuid-1", "service-uuid-2"]
}
```

#### Get All Patients
```http
GET /api/patients?facilityId=facility-uuid&page=0&size=10
```

#### Search Patients
```http
GET /api/patients/search?q=john&facilityId=facility-uuid
```

#### Get Patient by ID
```http
GET /api/patients/{id}
```

#### Update Patient
```http
PUT /api/patients/{id}
Content-Type: application/json

{
  "firstName": "John Updated",
  "lastName": "Doe",
  // ... other fields
}
```

#### Delete Patient
```http
DELETE /api/patients/{id}
```

### Facility Management

#### Get All Facilities
```http
GET /api/facilities
```

#### Create Facility
```http
POST /api/facilities
Content-Type: application/json

{
  "name": "General Hospital",
  "address": "456 Hospital Ave, Medical City, State 67890",
  "phoneNumber": "+1987654321",
  "email": "info@generalhospital.com",
  "type": "HOSPITAL"
}
```

### Service Management

#### Get All Services
```http
GET /api/services
```

#### Get Services by Type
```http
GET /api/services?type=LAB
```

### Health Check
```http
GET /api/health
```

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/emr/
│   │   ├── config/          # Configuration classes
│   │   │   ├── SecurityConfig.java
│   │   │   ├── CorsConfig.java
│   │   │   └── JpaConfig.java
│   │   ├── controller/      # REST controllers
│   │   │   ├── PatientController.java
│   │   │   ├── FacilityController.java
│   │   │   └── ServiceController.java
│   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── PatientDTO.java
│   │   │   ├── FacilityDTO.java
│   │   │   └── ServiceDTO.java
│   │   ├── entity/         # JPA entities
│   │   │   ├── Patient.java
│   │   │   ├── Facility.java
│   │   │   └── Service.java
│   │   ├── exception/      # Exception handling
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── ResourceNotFoundException.java
│   │   ├── repository/     # JPA repositories
│   │   │   ├── PatientRepository.java
│   │   │   ├── FacilityRepository.java
│   │   │   └── ServiceRepository.java
│   │   ├── service/        # Business logic
│   │   │   ├── PatientService.java
│   │   │   ├── FacilityService.java
│   │   │   └── ServiceService.java
│   │   └── EmrApplication.java
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       ├── application-prod.yml
│       └── data.sql
└── test/
    ├── java/com/emr/
    │   ├── config/         # Test configurations
    │   ├── controller/     # Controller tests
    │   ├── integration/    # Integration tests
    │   ├── service/        # Service tests
    │   └── util/          # Test utilities
    └── resources/
        └── application-test.yml
```

## 🐳 Docker Deployment

### Build Docker Image
```bash
# Build application
mvn clean package -DskipTests

# Build Docker image
docker build -t emr-backend:latest .
```

### Production Deployment
```bash
# Start production environment
docker-compose -f docker-compose.prod.yml up -d
```

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | Database host | localhost |
| `DB_PORT` | Database port | 5432 |
| `DB_NAME` | Database name | emr_db |
| `DB_USERNAME` | Database username | emr_user |
| `DB_PASSWORD` | Database password | emr_password |
| `KEYCLOAK_URL` | Keycloak server URL | http://localhost:8080 |
| `KEYCLOAK_REALM` | Keycloak realm | emr-realm |
| `CORS_ORIGINS` | Allowed CORS origins | http://localhost:3000 |

### Profiles

- **dev**: Development profile with debug logging
- **test**: Testing profile with H2 database
- **prod**: Production profile with optimized settings

## 🚀 Performance & Monitoring

### Health Checks
- **Application Health**: `/api/health`
- **Database Health**: `/api/health/db`
- **Keycloak Health**: `/api/health/keycloak`

### Metrics
- Spring Boot Actuator endpoints available at `/api/actuator/*`
- Custom metrics for patient operations
- Database connection pool monitoring

## 🔒 Security Best Practices

1. **JWT Token Validation**: All tokens validated against Keycloak
2. **Role-based Authorization**: Method-level security annotations
3. **Input Validation**: Bean validation on all DTOs
4. **SQL Injection Prevention**: JPA/Hibernate parameterized queries
5. **CORS Configuration**: Restricted to frontend origins only
6. **HTTPS**: Enforced in production environments

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For technical questions or issues:
- Create an issue on GitHub
- Check the logs for detailed error messages
- Ensure Keycloak is properly configured
- Verify database connectivity

## 🔄 Version History

- **v1.0.0**: Initial release with basic patient management
- **v1.1.0**: Added Keycloak integration and multi-facility support
- **v1.2.0**: Enhanced search capabilities and service management
- **v1.3.0**: Added comprehensive testing and Docker support