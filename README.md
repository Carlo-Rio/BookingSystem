# Conference Room Booking System

A REST API for managing conference room reservations. Users browse and book available rooms based on filters. The system automatically selects the most suitable room, handles scheduling conflicts, and sends email notifications.

---

## About the Project

The system was built to solve a common workplace problem — managing shared conference room bookings efficiently. Instead of manually picking a room, users provide their requirements and the system finds the best available option automatically.

Key behaviours:
- Rooms are booked automatically based on location, capacity, and time slot
- Double bookings are prevented at the database level using pessimistic locking
- Every action in the system is tracked in an audit log
- Users receive email confirmations and reminders 24 hours before their reservation
- Admins can manage users, rooms, and reservations independently

---

## Technologies Used

- **Java 21**
- **Spring Boot 3.5**
- **Spring Security** with JWT authentication
- **Spring Data JPA** / Hibernate
- **MySQL 8**
- **Docker** / Docker Compose
- **GitHub Actions** for CI
- **Swagger** for API documentation
- **JUnit 5** / Mockito for testing
- **Spring Mail** / Thymeleaf for email notifications

---

## Prerequisites

Before running this project make sure you have installed:

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) — required for Option 1
- [Java 21](https://adoptium.net/) — required for Option 2
- [Maven](https://maven.apache.org/) — required for Option 2
- [MySQL 8](https://dev.mysql.com/downloads/) — required for Option 2

---

## Installation

### Option 1 — Docker (recommended)

No additional setup required. Docker handles everything.

**1. Clone the repository**
```bash
git clone https://github.com/Carlo-Rio/BookingSystem.git
cd BookingSystem
```

**2. Start the application**
```bash
docker-compose up --build
```

**3. Wait for both containers to start**

You will see this when ready:
```
booking_app   | Started V1Application in X seconds
```

The API is now available at:
```
http://localhost:8080
```

---

### Option 2 — Run locally

**1. Clone the repository**
```bash
git clone https://github.com/Carlo-Rio/BookingSystem.git
cd BookingSystem
```

**2. Create the database**
```sql
CREATE DATABASE booking_system;
```

**3. Configure credentials**

Open `src/main/resources/application.properties` and update:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/booking_system
spring.datasource.username=your_username
spring.datasource.password=your_password
```

**4. Start the application**
```bash
./mvnw spring-boot:run
```

The API is now available at:
```
http://localhost:8080
```

---

## Getting Started

### Step 1 — Explore the API

Once the application is running open Swagger UI in your browser:
```
http://localhost:8080/swagger-ui/index.html
```

All endpoints are documented here. You can read what each one does and test them directly from the browser.

---

### Step 2 — Default admin account

On first startup the system creates an admin account automatically:

| Email | Password |
|---|---|
| admin@bookingsystem.com | admin123 |

---

### Step 3 — Register a user account

```
POST /api/users/register

{
    "username": "johndoe",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "password123"
}
```

---

### Step 4 — Log in

```
POST /api/auth/login

{
    "email": "john@example.com",
    "password": "password123"
}
```

You will receive a JWT token in the response:

```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "email": "john@example.com",
    "username": "johndoe",
    "role": "USER"
}
```

Copy the token. Add it to every subsequent request as a header:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

In Swagger UI click the **Authorize** button at the top, paste the token, and all requests will include it automatically.

---

### Step 5 — Create a room (as admin)

Log in as admin first, then:

```
POST /api/admin/resources

{
    "resourceName": "Conference Room A",
    "location": "FLOOR_1",
    "capacity": 10,
    "roomNumber" : 55
}
```

Available locations: `GROUND_FLOOR`, `FLOOR_1`, `FLOOR_2`, `FLOOR_3`

---

### Step 6 — Make a reservation (as user)

Log in as a regular user, then:

```
POST /api/reservations

{
    "startTime": "2026-06-18T10:00:00",
    "endTime": "2026-06-18T12:00:00",
    "location": "FLOOR_1",
    "capacity": 5
}
```

The system automatically selects the smallest available room that fits your requirements. You will receive a confirmation email immediately.

Reservation rules:
- Must be in the future
- Minimum 1 hour, maximum 2 hours
- Cannot overlap with existing reservations

---

### Step 7 — View your reservations

```
GET /api/reservations/my
Authorization: Bearer <token>
```

Supports pagination:
```
GET /api/reservations/my?page=0&size=10&sortBy=startTime&direction=desc
```

---

### Step 8 — Log out

```
POST /api/auth/logout
Authorization: Bearer <token>
```

The token is invalidated immediately. Any subsequent requests with the old token will be rejected.

---

## Running Tests

```bash
./mvnw test
```

---

## Challenges

**Preventing double bookings under concurrency** — the biggest technical challenge was ensuring two users cannot book the same room simultaneously. Solved using pessimistic locking at the database level combined with a JPQL overlap detection query that checks both `CONFIRMED` reservations within the requested time window.

**JWT with session-like behaviour** — implementing single-session enforcement with stateless JWT required storing the current active token per user and blacklisting tokens on logout, since JWT is inherently stateless and the server has no session to invalidate.

**Email reminders on server restart** — the scheduled reminder system needed to survive application restarts without sending duplicate emails. Solved by persisting a `reminderSent` flag directly on the reservation entity so the scheduler queries the database state rather than relying on in-memory state.

**Security layer design** — separating user and admin responsibilities cleanly required careful design of two distinct service interfaces, two sets of endpoints, and Spring Security rules enforced at both URL and method level to prevent privilege escalation.

---

## Future Developments

- [ ] Recurring reservations — weekly or daily booking patterns
- [ ] Thymeleaf frontend
- [ ] MapStruct for automatic entity mapping
- [ ] Flyway database migrations
- [ ] Deployment to Railway with CD pipeline
- [ ] Email verification on registration
- [ ] Password reset via email
- [ ] Room capacity warnings when nearly full
