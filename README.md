# 🎓 Student Management System

A full-stack Student Management System built with
Spring Boot and HTML/CSS.

## 🌐 Live Demo
[View Frontend](https://sivaram-30.github.io/student-management-system/)

## 🛠️ Tech Stack
- Java 21
- Spring Boot 3.5.11
- Spring Security + JWT
- MySQL 8
- Hibernate/JPA
- Docker
- HTML
- Maven

## 🚀 How to Run

### Normal Run
```bash
mvn spring-boot:run
```

### Docker Run
```bash
docker-compose up --build
```

## 🔐 Default Users
| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| teacher | teacher123 | TEACHER |

## 📡 API Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/login | Login |
| GET | /api/students | Get all students |
| POST | /api/students | Create student |
| PUT | /api/students/{id} | Update student |
| DELETE | /api/students/{id} | Delete student |

## 👩‍💻 Author
Sivaram — [GitHub](https://github.com/Sivaram-30)
