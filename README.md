[README.md](https://github.com/user-attachments/files/28729385/README.md)
# Booking System — Conference Room Reservation API

A RESTful API for managing conference room reservations built with Spring Boot. The system allows users to browse available rooms, make reservations based on filters, and manage their bookings. Administrators have full control over users, resources, and reservations. The booking system also has audit logs that monitors every actions made by an administrator or an user.


---

## Features

- **User management** — registration, login, profile editing, password change
- **Role-based access control** — separate permissions for users and admins
- **Room browsing** — filter by location, capacity, and availability
- **Smart reservation** — system automatically selects the best available room based on user filters
- **Overlap detection** — prevents double booking with pessimistic locking
- **Session management** — single login enforcement per user
- **Audit logging** — tracks all important actions across the system
- **Input validation** — meaningful error messages for invalid requests
- **API documentation** — Swagger UI available at `/swagger-ui/index.html`

---

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.0 |
| Security | Spring Security |
| ORM | Spring Data JPA / Hibernate 6 |
| Database | MySQL 8 |
| Build tool | Maven |
| Containerization | Docker / Docker Compose |
| API Documentation | SpringDoc OpenAPI (Swagger) |
| Testing | JUnit 5 / Mockito |
| Utilities | Lombok |

---

## Prerequisites

- Java 21
- Maven 3.9+
- MySQL 8 (or Docker)
- Docker and Docker Compose (optional)

---

## Getting Started

### Option 1 — Run with Docker (recommended)

Clone the repository:
```bash
git clone https://github.com/Carlo-Rio/booking-system.git
cd booking-system
```

Start the application:
```bash
docker-compose up --build
```

The application will be available at `http://localhost:8080`

### Option 2 — Run locally

**1. Clone the repository:**
```bash
git clone https://github.com/Carlo-Rio/booking-system.git
cd booking-system
```

**2. Create a MySQL database:**
```sql
CREATE DATABASE booking_system;
```

**3. Update `application.properties`:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/booking_system
spring.datasource.username=your_username
spring.datasource.password=your_password
```

**4. Run the application:**
```bash
./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`

---

## Default Credentials

On first startup the system automatically creates an admin account:

| Role | Email | Password |
|---|---|---|
| Admin | admin@bookingsystem.com | admin123 |

Register a regular user account via `POST /api/users/register`

---

## API Documentation

Swagger UI is available at:
```
http://localhost:8080/swagger-ui/index.html
```

### Main endpoints

**Authentication:**
```
POST   /api/auth/login           Login
POST   /api/auth/logout          Logout
POST   /api/users/register       Register new user
```

**Resources (rooms):**
```
GET    /api/resources                          Get all active rooms
GET    /api/resources/{id}                     Get room by ID
GET    /api/resources/location?location=       Filter by location
GET    /api/resources/capacity?capacity=       Filter by exact capacity
GET    /api/resources/capacity/exact?capacity= Filter by minimum capacity
GET    /api/resources/available?start=&end=    Get available rooms for time slot
GET    /api/resources/filter                   Filter by all criteria
```

**Reservations:**
```
POST   /api/reservations                           Make a reservation
GET    /api/reservations/my                        View my reservations
PATCH  /api/reservations/{id}/reschedule           Reschedule reservation
DELETE /api/reservations/{id}/cancel               Cancel reservation
```

**Admin — User management:**
```
GET    /api/admin/users                            Get all users
GET    /api/admin/users/{id}                       Get user by ID
GET    /api/admin/users/search/email?email=        Search by email
GET    /api/admin/users/search/username?username=  Search by username
PUT    /api/admin/users/{id}/block                 Block user
PUT    /api/admin/users/{id}/activate              Activate user
DELETE /api/admin/users/{id}                       Delete user
```

**Admin — Resource management:**
```
GET    /api/admin/resources                    Get all resources
POST   /api/admin/resources                    Create resource
PUT    /api/admin/resources/{id}               Edit resource
DELETE /api/admin/resources/{id}               Delete resource
PUT    /api/admin/resources/{id}/activate      Activate resource
PUT    /api/admin/resources/{id}/deactivate    Deactivate resource
```

**Admin — Reservation management:**
```
GET    /api/admin/reservations                 Get all reservations
GET    /api/admin/reservations/{id}            Get reservation by ID
PUT    /api/admin/reservations/{id}/confirm    Confirm reservation
PUT    /api/admin/reservations/{id}/cancel     Cancel reservation
```

**Admin — Audit logs:**
```
GET    /api/admin/audit-logs                          Get all audit logs
GET    /api/admin/audit-logs/{id}                     Get audit log by ID
GET    /api/admin/audit-logs/action?action=           Filter by action
```

---

## Making a Reservation

The system automatically selects the best available room based on your filters:

```json
POST /api/reservations
{
    "startTime": "2026-06-10T10:00:00",
    "endTime": "2026-06-10T12:00:00",
    "location": "FLOOR_1",
    "capacity": 10
}
```

The system will:
1. Validate the time slot (must be in the future, max 2 hours, min 1 hour)
2. Find all rooms matching your location and capacity on that floor
3. Check for overlapping reservations
4. Select the smallest room that fits your requirements
5. Create a `PENDING` reservation

---


---

## Reservation Status Flow

```
PENDING → CONFIRMED (admin confirms)
PENDING → CANCELLED (user or admin cancels)
CONFIRMED → CANCELLED (user or admin cancels)
```

---

## Running Tests

```bash
./mvnw test
```

---

## Project Structure

```
src/main/java/com/booking/system/v1/
├── configuration/       Spring Security and session configuration
├── controller/          REST controllers
├── dto/                 Data Transfer Objects
├── entity/              JPA entities
├── exception/           Custom exceptions and global handler
├── mapper/              Entity to DTO mappers
├── repository/          Spring Data JPA repositories
└── service/
    ├── impl/            Service implementations
    └── *.java           Service interfaces
```


---

## License

MIT License — see [LICENSE](LICENSE) for details.
