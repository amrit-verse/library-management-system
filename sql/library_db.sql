-- ============================================================
--  Library Management System - MySQL Database Schema
--  File   : library_db.sql
--  Author : Library System
--  Desc   : Creates all tables, relationships, and sample data
-- ============================================================

-- Step 1: Create and select database
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- ============================================================
-- TABLE: books
-- Stores all book details in the library
-- ============================================================
CREATE TABLE IF NOT EXISTS books (
    book_id      VARCHAR(10)  PRIMARY KEY,
    book_name    VARCHAR(100) NOT NULL,
    author_name  VARCHAR(100) NOT NULL,
    category     VARCHAR(50)  NOT NULL,
    quantity     INT          NOT NULL DEFAULT 0,
    isbn         VARCHAR(20)  UNIQUE NOT NULL,
    added_date   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_quantity CHECK (quantity >= 0)
);

-- ============================================================
-- TABLE: users
-- Stores registered library users and admin accounts
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    user_id      INT          AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50)  UNIQUE NOT NULL,
    password     VARCHAR(100) NOT NULL,
    email        VARCHAR(100) UNIQUE NOT NULL,
    phone        VARCHAR(15),
    role         ENUM('USER', 'ADMIN') DEFAULT 'USER',
    created_date TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLE: transactions
-- Records every borrow and return event
-- Fine = Rs. 5 per day beyond due_date (14-day borrow period)
-- ============================================================
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id  INT            AUTO_INCREMENT PRIMARY KEY,
    book_id         VARCHAR(10)    NOT NULL,
    user_id         INT            NOT NULL,
    borrow_date     DATE           NOT NULL,
    due_date        DATE           NOT NULL,
    return_date     DATE           DEFAULT NULL,
    fine_amount     DECIMAL(10,2)  DEFAULT 0.00,
    status          ENUM('BORROWED', 'RETURNED') DEFAULT 'BORROWED',
    CONSTRAINT fk_book        FOREIGN KEY (book_id)  REFERENCES books(book_id)  ON UPDATE CASCADE,
    CONSTRAINT fk_user        FOREIGN KEY (user_id)  REFERENCES users(user_id)  ON UPDATE CASCADE
);

-- ============================================================
-- DEFAULT ADMIN ACCOUNT
-- Username : admin
-- Password : admin123
-- ============================================================
INSERT INTO users (username, password, email, phone, role)
VALUES ('admin', 'admin123', 'admin@library.com', '9999999999', 'ADMIN')
ON DUPLICATE KEY UPDATE username = username;

-- ============================================================
-- SAMPLE BOOKS
-- ============================================================
INSERT INTO books (book_id, book_name, author_name, category, quantity, isbn) VALUES
('BK001', 'The Great Gatsby',             'F. Scott Fitzgerald', 'Fiction',    5, '978-0743273565'),
('BK002', 'Clean Code',                   'Robert C. Martin',    'Technology', 3, '978-0132350884'),
('BK003', 'Introduction to Algorithms',   'Thomas H. Cormen',    'Technology', 4, '978-0262033848'),
('BK004', 'To Kill a Mockingbird',         'Harper Lee',          'Fiction',    6, '978-0061935466'),
('BK005', 'Design Patterns',              'Gang of Four',        'Technology', 2, '978-0201633610'),
('BK006', 'Atomic Habits',               'James Clear',         'Self-Help',  5, '978-0735211292'),
('BK007', 'The Alchemist',               'Paulo Coelho',        'Fiction',    4, '978-0062315007'),
('BK008', 'Data Structures in Java',     'Robert Lafore',       'Technology', 3, '978-0672324536'),
('BK009', 'Operating System Concepts',   'Silberschatz',        'Technology', 4, '978-1119800361'),
('BK010', 'Rich Dad Poor Dad',           'Robert Kiyosaki',     'Finance',    5, '978-1612680194')
ON DUPLICATE KEY UPDATE book_name = book_name;

-- ============================================================
-- SAMPLE USERS
-- ============================================================
INSERT INTO users (username, password, email, phone, role) VALUES
('john_doe',    'password123', 'john@example.com',  '9876543210', 'USER'),
('jane_smith',  'pass456',     'jane@example.com',  '9876543211', 'USER'),
('raj_kumar',   'raj@123',     'raj@example.com',   '9876543212', 'USER')
ON DUPLICATE KEY UPDATE username = username;

-- ============================================================
-- SAMPLE TRANSACTIONS (for testing history and fine)
-- ============================================================
INSERT INTO transactions (book_id, user_id, borrow_date, due_date, return_date, fine_amount, status) VALUES
('BK001', 2, '2025-01-01', '2025-01-15', '2025-01-14', 0.00,  'RETURNED'),
('BK002', 2, '2025-02-01', '2025-02-15', '2025-02-20', 25.00, 'RETURNED'),
('BK003', 3, CURDATE(),    DATE_ADD(CURDATE(), INTERVAL 14 DAY), NULL, 0.00, 'BORROWED');

-- ============================================================
-- USEFUL VIEWS (optional but helpful during demo)
-- ============================================================
CREATE OR REPLACE VIEW active_borrows AS
SELECT 
    t.transaction_id,
    u.username,
    b.book_name,
    b.author_name,
    t.borrow_date,
    t.due_date,
    DATEDIFF(CURDATE(), t.due_date) AS days_overdue
FROM transactions t
JOIN users u ON t.user_id  = u.user_id
JOIN books b ON t.book_id  = b.book_id
WHERE t.status = 'BORROWED';

SELECT 'Database setup complete!' AS message;
