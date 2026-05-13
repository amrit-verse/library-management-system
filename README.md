# Library Management System

**Modern Full Stack Library Management System**  
Built with **Spring Boot, MySQL, HTML, CSS, and Vanilla JavaScript**

A professional full-stack Library Management System developed as a **Final Year Computer Science Engineering Project**.  
The project features a modern responsive frontend, REST API backend, role-based dashboards, CRUD operations, live search, session-based authentication, and cloud deployment.

---

# Live Demo

## Frontend (Vercel)

```text
https://library-management-system-gold-nine.vercel.app
```

## Backend API (Render)

```text
https://library-management-system-wgmk.onrender.com
```

## Books API

```text
https://library-management-system-wgmk.onrender.com/books
```

---

# Project Overview

The system provides:

- Professional Admin Dashboard
- User Dashboard
- Book Management System
- Live Book Search
- Role-Based Login
- Responsive UI
- REST API Integration
- Cloud Deployment

The application demonstrates:

- Full Stack Development
- REST API Architecture
- Database Integration
- Responsive Frontend Design
- CRUD Operations
- Session-Based Authentication
- Modern UI/UX Design

---

# Features

## Authentication System

- Admin Login
- User Login
- Role-Based Redirects
- Session Handling
- Logout System
- Route Protection

### Demo Credentials

| Role | Email | Password |
|---|---|---|
| Admin | admin@library.com | admin123 |
| User | user@library.com | user123 |

---

# Admin Features

- View Dashboard Statistics
- Add Books
- Edit Books
- Delete Books
- Search Books
- Manage Library Collection
- Responsive Sidebar Navigation
- Live Book Data from Backend API

---

# User Features

- User Dashboard
- Search Books
- Browse Available Books
- Session-Based Access
- Responsive UI

---

# Book Management Features

- Add New Books
- Update Existing Books
- Delete Books
- Dynamic Search
- Search by:
  - Title
  - Author
  - Category
- Live Table Rendering
- Loading States
- Empty States

---

# UI/UX Features

- Modern Responsive Design
- Gradient Login Page
- Professional Dashboard Layout
- Sidebar Navigation
- Responsive Tables
- Modern Cards & Forms
- Hover Animations
- Mobile Responsive Layout
- Clean Academic Software Theme

---

# Tech Stack

| Layer | Technology |
|---|---|
| Frontend | HTML5 |
| Styling | CSS3 |
| Frontend Logic | Vanilla JavaScript |
| Backend | Spring Boot 3.5 |
| Language | Java 17 |
| ORM | Spring Data JPA / Hibernate |
| Database | MySQL |
| Hosting | Vercel |
| Backend Hosting | Render |
| Database Hosting | Railway |

---

# Project Structure

```text
LibraryManagementSystem/
│
├── ModernLibraryFrontend/
│   ├── css/
│   │   └── style.css
│   │
│   ├── js/
│   │   ├── app.js
│   │   └── auth.js
│   │
│   ├── pages/
│   │   ├── login.html
│   │   ├── admin-dashboard.html
│   │   ├── user-dashboard.html
│   │   ├── manage-books.html
│   │   ├── search-books.html
│   │   ├── borrow-books.html
│   │   ├── return-books.html
│   │   └── history.html
│   │
│   └── index.html
│
├── library-backend/
│   ├── controller/
│   ├── entity/
│   ├── repository/
│   ├── service/
│   └── config/
│
└── README.md
```

---

# Backend API

## Base URL

```text
https://library-management-system-wgmk.onrender.com
```

## Available Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/books` | Fetch all books |
| POST | `/books` | Add new book |
| PUT | `/books/{id}` | Update book |
| DELETE | `/books/{id}` | Delete book |

---

# Database Configuration

The backend uses Railway MySQL with environment variables.

## application.properties

```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
```

---

# Local Development

## Clone Repository

```bash
git clone https://github.com/amrit-verse/library-management-system.git

cd library-management-system
```

---

# Frontend Setup

```bash
cd ModernLibraryFrontend

python3 -m http.server 5500
```

Open:

```text
http://localhost:5500
```

---

# Backend Setup

```bash
cd library-backend
```

Run Spring Boot application using:

- IntelliJ IDEA
- VS Code
- Maven
- Terminal

---

# Deployment

## Frontend Deployment

- Hosted on Vercel
- Connected with GitHub repository
- Automatic deployment on push

## Backend Deployment

- Hosted on Render
- Connected with Railway MySQL database

---

# Current Project Status

## Completed

- Full Frontend UI
- Admin Dashboard
- User Dashboard
- CRUD Operations
- Search System
- Authentication System
- Responsive Design
- REST API Integration
- Cloud Deployment

## Planned Future Improvements

- JWT Authentication
- Spring Security
- Real Borrow/Return Backend System
- Borrow History Persistence
- User Registration
- Analytics Dashboard
- Fine Calculation System
- Email Notifications

---

# Learning Outcomes

This project helped in understanding:

- Full Stack Development
- REST APIs
- Spring Boot
- Database Connectivity
- Frontend Architecture
- Deployment Workflow
- Responsive UI Design
- Session Management
- CRUD Operations
- Git & GitHub Workflow

---

# Author

## Amrit

Computer Science Engineering Student

Built as a professional academic full-stack project using modern web technologies.
