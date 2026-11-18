-- =========================================================
-- UniRoom — Smart University Room Reservation System
-- Database Schema for MySQL
-- =========================================================

CREATE DATABASE IF NOT EXISTS uniroom;
USE uniroom;

-- ---------------------------------------------------------
-- Table: users
-- Stores all system users (students, teachers, admins)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'TEACHER', 'ADMIN') NOT NULL DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- Table: rooms
-- Stores all university rooms and their details
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    capacity INT NOT NULL DEFAULT 30,
    equipment VARCHAR(500) DEFAULT '',
    status ENUM('AVAILABLE', 'MAINTENANCE', 'CLOSED') NOT NULL DEFAULT 'AVAILABLE',
    building VARCHAR(100) DEFAULT '',
    floor_number INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- Table: reservations
-- Stores all room reservations with QR code data
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    room_id INT NOT NULL,
    reservation_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status ENUM('CONFIRMED', 'CANCELLED', 'COMPLETED', 'PENDING') NOT NULL DEFAULT 'CONFIRMED',
    purpose VARCHAR(300) DEFAULT '',
    qr_code VARCHAR(500) DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Table: usage_statistics
-- Tracks room usage for analytics
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS usage_statistics (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_id INT NOT NULL,
    usage_date DATE NOT NULL,
    hour_of_day INT NOT NULL,
    usage_count INT DEFAULT 1,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Table: timetable
-- Stores weekly class schedules (emplois du temps)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS timetable (
    id INT AUTO_INCREMENT PRIMARY KEY,
    day_of_week ENUM('LUNDI', 'MARDI', 'MERCREDI', 'JEUDI', 'VENDREDI', 'SAMEDI') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    subject VARCHAR(200) NOT NULL,
    teacher_id INT,
    room_id INT,
    group_name VARCHAR(100) DEFAULT 'Tous',
    semester VARCHAR(50) DEFAULT 'S1',
    type ENUM('COURS', 'TD', 'TP', 'EXAM') NOT NULL DEFAULT 'COURS',
    color VARCHAR(7) DEFAULT '#6366F1',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE SET NULL
);

-- =========================================================
-- Sample Data
-- =========================================================

-- Default admin account (password: admin123)
INSERT INTO users (name, email, password, role) VALUES
('Admin User', 'admin@fst.com', 'admin123', 'ADMIN'),
('Prof. prof', 'prof@fst.com', 'teacher123', 'TEACHER'),
('Prof. prof2', 'prof2@fst.com', 'teacher123', 'TEACHER'),
('Mehdi Student', 'mehdi@fst.com', 'student123', 'STUDENT'),
('mehdi','mehdi','mehdi','ADMIN'),
('ahmed Student', 'ahmed@fst.com', 'student123', 'STUDENT'),
('mohamed Student', 'mohamed@fst.com', 'student123', 'STUDENT');

-- Sample rooms
INSERT INTO rooms (name, capacity, equipment, status, building, floor_number) VALUES
('Amphitheatre A1', 200, 'Projector, Microphone, Speakers', 'AVAILABLE', 'Building A', 0),
('Lab Info 101', 30, 'Computers, Projector, Whiteboard', 'AVAILABLE', 'Building B', 1),
('Lab Info 102', 25, 'Computers, Projector', 'AVAILABLE', 'Building B', 1),
('Salle TD 201', 40, 'Projector, Whiteboard', 'AVAILABLE', 'Building A', 2),
('Salle TD 202', 35, 'Projector, Whiteboard', 'AVAILABLE', 'Building A', 2),
('Lab Physique', 20, 'Lab Equipment, Projector', 'AVAILABLE', 'Building C', 1),
('Salle Conference', 50, 'Projector, Microphone, Video Conference', 'AVAILABLE', 'Building A', 3),
('Salle Etude 301', 15, 'Whiteboard', 'AVAILABLE', 'Building B', 3),
('Lab Chimie', 20, 'Lab Equipment, Fume Hood', 'MAINTENANCE', 'Building C', 2),
('Amphitheatre B1', 150, 'Projector, Microphone, Speakers, Recording', 'AVAILABLE', 'Building B', 0);

-- Sample reservations
INSERT INTO reservations (user_id, room_id, reservation_date, start_time, end_time, status, purpose) VALUES
(2, 1, CURDATE(), '08:00', '10:00', 'CONFIRMED', 'Mathematics Lecture'),
(2, 4, CURDATE(), '10:00', '12:00', 'CONFIRMED', 'Physics TD'),
(3, 2, CURDATE(), '14:00', '16:00', 'CONFIRMED', 'Programming Lab'),
(4, 5, CURDATE(), '08:00', '10:00', 'CONFIRMED', 'Study Group'),
(5, 8, CURDATE(), '10:00', '12:00', 'CONFIRMED', 'Project Meeting'),
(4, 7, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '14:00', '16:00', 'CONFIRMED', 'Presentation Prep'),
(3, 10, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '08:00', '10:00', 'CONFIRMED', 'Electronics Lecture');

-- Sample statistics
INSERT INTO usage_statistics (room_id, usage_date, hour_of_day, usage_count) VALUES
(1, CURDATE(), 8, 5), (1, CURDATE(), 9, 4), (1, CURDATE(), 10, 6),
(2, CURDATE(), 14, 3), (2, CURDATE(), 15, 3),
(4, CURDATE(), 8, 2), (4, CURDATE(), 10, 4),
(7, CURDATE(), 14, 1),
(10, CURDATE(), 8, 3), (10, CURDATE(), 9, 3);

-- Sample timetable (emploi du temps)
INSERT INTO timetable (day_of_week, start_time, end_time, subject, teacher_id, room_id, group_name, semester, type, color) VALUES
-- LUNDI
('LUNDI', '08:00', '10:00', 'Mathématiques', 2, 1, 'Groupe A', 'S1', 'COURS', '#6366F1'),
('LUNDI', '10:00', '12:00', 'Programmation Java', 3, 2, 'Groupe A', 'S1', 'TP', '#22C55E'),
('LUNDI', '14:00', '16:00', 'Base de Données', 2, 4, 'Groupe A', 'S1', 'TD', '#F59E0B'),
('LUNDI', '16:00', '18:00', 'Anglais', 3, 5, 'Groupe B', 'S1', 'TD', '#EC4899'),
-- MARDI
('MARDI', '08:00', '10:00', 'Physique', 3, 1, 'Tous', 'S1', 'COURS', '#8B5CF6'),
('MARDI', '10:00', '12:00', 'Algorithmique', 2, 4, 'Groupe A', 'S1', 'TD', '#3B82F6'),
('MARDI', '14:00', '16:00', 'Réseaux', 3, 2, 'Groupe B', 'S1', 'TP', '#EF4444'),
-- MERCREDI
('MERCREDI', '08:00', '10:00', 'Systèmes d''Exploitation', 2, 1, 'Tous', 'S1', 'COURS', '#14B8A6'),
('MERCREDI', '10:00', '12:00', 'Mathématiques', 2, 4, 'Groupe B', 'S1', 'TD', '#6366F1'),
-- JEUDI
('JEUDI', '08:00', '10:00', 'Programmation Java', 3, 1, 'Tous', 'S1', 'COURS', '#22C55E'),
('JEUDI', '10:00', '12:00', 'Base de Données', 2, 3, 'Groupe A', 'S1', 'TP', '#F59E0B'),
('JEUDI', '14:00', '16:00', 'Physique', 3, 6, 'Groupe A', 'S1', 'TP', '#8B5CF6'),
('JEUDI', '16:00', '18:00', 'Projet Tutoré', 2, 8, 'Groupe B', 'S1', 'TP', '#F97316'),
-- VENDREDI
('VENDREDI', '08:00', '10:00', 'Réseaux', 3, 1, 'Tous', 'S1', 'COURS', '#EF4444'),
('VENDREDI', '10:00', '12:00', 'Algorithmique', 2, 2, 'Groupe B', 'S1', 'TP', '#3B82F6'),
('VENDREDI', '14:00', '16:00', 'Anglais', 3, 7, 'Groupe A', 'S1', 'TD', '#EC4899');

