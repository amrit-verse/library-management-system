# Library Management System — Java

**Final Year Project | Computer Science Engineering**

A fully functional, menu-driven Library Management System built in **Java** with **MySQL** database connectivity via **JDBC**. Designed to demonstrate core **Object-Oriented Programming** principles and clean software architecture.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Tech Stack](#tech-stack)
3. [OOP Concepts Used](#oop-concepts-used)
4. [Features](#features)
5. [Project Structure](#project-structure)
6. [Database Setup](#database-setup)
7. [How to Run](#how-to-run)
8. [Sample Credentials](#sample-credentials)
9. [Fine Policy](#fine-policy)
10. [Author](#author)

---

## Project Overview

The Library Management System allows:
- **Admins** to manage the book catalog and monitor users/transactions.
- **Users** to register, search for books, borrow them, and return them.
- Automatic **fine calculation** for overdue returns.
- Persistent **audit logging** to `library_log.txt`.

---

## Tech Stack

| Layer       | Technology             |
|-------------|------------------------|
| Language    | Java 17+               |
| Database    | MySQL 8.x              |
| DB Driver   | MySQL Connector/J 8.x  |
| IDE         | IntelliJ IDEA / Eclipse / VS Code |
| Build       | Manual (javac + java)  |

---

## OOP Concepts Used

| Concept            | Where Applied                                          |
|--------------------|--------------------------------------------------------|
| **Classes/Objects**| Book, User, Admin, Transaction, LibraryService         |
| **Encapsulation**  | All model fields are private; accessed via getters/setters |
| **Inheritance**    | `Admin extends User`                                   |
| **Polymorphism**   | `getRole()` overridden in Admin; `login()` returns User or Admin |
| **Abstraction**    | Service layer hides SQL from Main; ValidationUtil hides regex |
| **Interfaces**     | (Extendable: add Borrowable, Searchable interfaces)    |
| **Exception Handling** | Custom `BookNotFoundException`, `InvalidUserException` |
| **Collections**    | `List<Book>`, `List<User>`, `List<String>` throughout  |
| **File Handling**  | `FileUtil` logs to `library_log.txt` using `BufferedWriter` |
| **JDBC**           | `DBConnection` (Singleton), PreparedStatements, ResultSets |

---

## Features

### Admin
- Add new books (auto-generated Book ID)
- Update book details
- Delete books (blocked if copies are borrowed)
- View all books (tabular format)
- Search books by title / author / category / ISBN
- View all registered users
- Delete users (blocked if active borrows exist)
- View all transactions system-wide

### User
- Register with username / email / phone validation
- Login (role-based session)
- Search books by any keyword
- Borrow a book (with duplicate-borrow check)
- Return a book (with automatic fine calculation)
- View personal borrow history

---

## Project Structure

```
LibraryManagementSystem/
├── src/
│   ├── main/
│   │   └── Main.java               ← Entry point
│   ├── model/
│   │   ├── Book.java               ← Book entity
│   │   ├── User.java               ← User entity (base class)
│   │   ├── Admin.java              ← Admin entity (extends User)
│   │   └── Transaction.java        ← Borrow/Return record
│   ├── service/
│   │   ├── LibraryService.java     ← Book + transaction logic
│   │   └── UserService.java        ← User management
│   ├── database/
│   │   └── DBConnection.java       ← Singleton JDBC connection
│   ├── util/
│   │   ├── ValidationUtil.java     ← Input validation helpers
│   │   └── FileUtil.java           ← File logging
│   └── exception/
│       ├── BookNotFoundException.java
│       └── InvalidUserException.java
├── sql/
│   └── library_db.sql              ← Full DB schema + sample data
├── lib/
│   └── mysql-connector-j-*.jar     ← Place JDBC driver here
├── library_log.txt                 ← Auto-created on first run
└── README.md
```

---

## Database Setup

1. Install MySQL 8.x and start the MySQL server.
2. Open MySQL Workbench or terminal.
3. Run the SQL script:

```sql
source /path/to/LibraryManagementSystem/sql/library_db.sql
```

Or copy-paste the contents of `sql/library_db.sql` into MySQL Workbench and execute.

4. Update `src/database/DBConnection.java` with your MySQL credentials:

```java
private static final String DB_URL      = "jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=UTC";
private static final String DB_USER     = "root";
private static final String DB_PASSWORD = "your_mysql_password";
```

---

## How to Run

### Step 1 — Download MySQL JDBC Driver
Download `mysql-connector-j-8.x.x.jar` from:
https://dev.mysql.com/downloads/connector/j/

Place it in the `lib/` folder.

### Step 2 — Compile

Open a terminal in the `LibraryManagementSystem/` root and run:

```bash
# Windows
javac -cp "lib/mysql-connector-j-8.x.x.jar" -d out src/database/*.java src/exception/*.java src/model/*.java src/util/*.java src/service/*.java src/main/*.java

# Mac/Linux
javac -cp "lib/mysql-connector-j-8.x.x.jar:." -d out src/database/*.java src/exception/*.java src/model/*.java src/util/*.java src/service/*.java src/main/*.java
```

### Step 3 — Run

```bash
# Windows
java -cp "out;lib/mysql-connector-j-8.x.x.jar" main.Main

# Mac/Linux
java -cp "out:lib/mysql-connector-j-8.x.x.jar" main.Main
```

### Using IntelliJ IDEA
1. Open project → File > Open → select `LibraryManagementSystem/`
2. Right-click `lib/mysql-connector-j-*.jar` → Add as Library
3. Right-click `src/main/Main.java` → Run

---

## Sample Credentials

| Role  | Username   | Password    |
|-------|------------|-------------|
| Admin | admin      | admin123    |
| User  | john_doe   | password123 |
| User  | jane_smith | pass456     |
| User  | raj_kumar  | raj@123     |

---

## Fine Policy

| Item           | Value         |
|----------------|---------------|
| Borrow period  | 14 days       |
| Fine rate      | Rs. 5 per day |
| Due date       | Borrow date + 14 days |
| Fine formula   | (Return date − Due date) × 5 |

---

## Author

> Final Year B.E./B.Tech CSE Project  
> Library Management System — Java + MySQL  
> Built with OOP, JDBC, and Clean Code principles.
