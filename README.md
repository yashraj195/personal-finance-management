# Personal Finance Management API

A secure, production-ready REST API for managing personal finances, transactions, budgets, and financial analytics.

Built with Spring Boot and deployed as a cloud-hosted backend with a persistent MySQL database.

## 📌 Project Highlights

- Secure JWT authentication <br>
- User-level data isolation <br>
- Transaction CRUD with filtering and pagination <br>
- Budget management and utilisation analytics <br>
- Financial analytics and monthly comparisons <br>
- Global exception handling <br>
- Bean validation <br>
- OpenAPI/Swagger documentation <br>
- Automated testing <br>
- Cloud-hosted MySQL database <br>
- Production deployment with HTTPS <br>

## 🚀 Live Demo

**API:**  
https://personal-finance-management-chi-ten.vercel.app

**Swagger UI:**  
https://personal-finance-management-chi-ten.vercel.app/swagger-ui/index.html

## ✨ Features

### Authentication & Security
- User registration and login
- JWT-based authentication
- Stateless Spring Security configuration
- Protected API endpoints
- User-specific data isolation

### Transaction Management
- Create, read, update and delete transactions
- Income and expense tracking
- Transaction categories
- Date-based filtering
- Pagination and sorting
- User-specific transaction access

### Budget Management
- Create and manage monthly budgets
- Category-based budgets
- Duplicate budget prevention
- Budget utilisation tracking
- Remaining budget calculation
- Budget status detection:
  - Within limit
  - Approaching limit
  - Exceeded

### Financial Analytics
- Monthly income and expense summary
- Net balance calculation
- Category-wise spending
- Highest spending category
- Monthly financial overview
- Month-to-month comparison
- Budget utilisation analytics

### Validation & Error Handling
- Jakarta Bean Validation
- Global exception handling
- Structured error responses
- Resource-not-found handling
- Duplicate-resource handling

### API Documentation
- OpenAPI documentation
- Interactive Swagger UI
- JWT authentication support in Swagger

## 🛠️ Tech Stack

### Backend
- Java 25 <br>
- Spring Boot <br>
- Spring Security <br>
- Spring Data JPA <br>
- Hibernate <br>
- REST APIs <br>
- JWT <br>
- Jakarta Bean Validation <br>

### Database
- MySQL <br>
- Aiven Cloud <br>

### API Documentation
- OpenAPI <br>
- Swagger UI <br>

### Build & Development
- Maven <br>
- IntelliJ IDEA <br>
- Git <br>
- GitHub <br>

### Deployment
- Vercel <br>
- Aiven MySQL <br>

## 🧪 Testing

The project includes automated unit and controller tests covering:

- Transaction service <br>
- Budget service <br>
- Transaction controller <br>
- Budget controller <br>
- Analytics controller <br>

### 29 tests passing successfully.

## 🔑 Authentication Flow

1. Register a user using `/api/auth/register`
2. Login using `/api/auth/login`
3. Copy the returned JWT token
4. Click **Authorize** in Swagger UI
5. Enter: Bearer <JWT_TOKEN>
6. Access the protected endpoints: All protected resources are associated with the authenticated user. <br>

## ☁️ Deployment Architecture

 ```text
         Client
           │
           ▼
    Vercel / HTTPS
           │
           ▼
  Spring Boot REST API
           │
   ┌───────┴───────┐
   │               │
   ▼               ▼
  JWT Auth   Business Logic
                   │
                   ▼
              Aiven MySQL
  ```

## 📁 Project Structure

```text
src/
├── main/
│   ├── java/com/yash/finance/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── exception/
│   │   ├── repository/
│   │   ├── security/
│   │   ├── service/
│   │   └── specification/
│   └── resources/
│       └── application.properties
│
└── test/
    └── java/com/yash/finance/
        ├── controller/
        └── service/
```

## 🏗️ Architecture diagram

```text
                    ┌──────────────────┐
                    │      Client      │
                    │ Swagger / React  │
                    └────────┬─────────┘
                             │ HTTPS
                             ▼
                    ┌──────────────────┐
                    │     Vercel       │
                    │  Spring Boot API │
                    └────────┬─────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
              ▼              ▼              ▼
        ┌──────────┐   ┌───────────┐   ┌───────────┐
        │  Spring  │   │   JWT +   │   │ Validation│
        │ Security │   │ Security  │   │ & Errors  │
        └──────────┘   └───────────┘   └───────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │   JPA / Hibernate│
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │   Aiven MySQL    │
                    │  Cloud Database  │
                    └──────────────────┘
```



## 👨‍💻 Author

### Yash Raj
