-- Sample data for TechCorp HelpDesk User Service
-- This file will be automatically executed when the application starts
-- Updated with Keycloak integration fields
-- Uses conditional inserts to avoid duplicates on restart

-- =====================================================
-- SYSTEM ADMINISTRATOR (Pre-configured)
-- =====================================================
-- Single system admin who manages the entire platform
INSERT INTO users (first_name, last_name, email, username, password, role, status, created_at, updated_at, approval_date, is_first_login, keycloak_user_id) 
SELECT 'Michael', 'Rodriguez', 'admin@techcorp.com', 'michael.rodriguez', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'ADMIN', 'ACTIVE', '2024-01-01 08:00:00', '2024-01-01 08:00:00', '2024-01-01 08:00:00', false, 'admin-keycloak-id'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@techcorp.com');

-- =====================================================
-- TECHNICIANS (IT Support Staff)
-- =====================================================
-- Senior IT Technicians - approved and active
INSERT INTO users (first_name, last_name, email, username, password, role, status, created_at, updated_at, approval_date, is_first_login, keycloak_user_id) 
SELECT 'Sarah', 'Johnson', 'sarah.johnson@techcorp.com', 'sarah.johnson', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'TECHNICIAN', 'ACTIVE', '2024-01-02 09:15:00', '2024-01-02 10:30:00', '2024-01-02 10:30:00', false, 'sarah-johnson-keycloak-id'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'sarah.johnson@techcorp.com');

INSERT INTO users (first_name, last_name, email, username, password, role, status, created_at, updated_at, approval_date, is_first_login, keycloak_user_id) 
SELECT 'David', 'Chen', 'david.chen@techcorp.com', 'david.chen', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'TECHNICIAN', 'ACTIVE', '2024-01-03 08:45:00', '2024-01-03 11:15:00', '2024-01-03 11:15:00', false, 'david-chen-keycloak-id'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'david.chen@techcorp.com');

INSERT INTO users (first_name, last_name, email, username, password, role, status, created_at, updated_at, approval_date, is_first_login, keycloak_user_id) 
SELECT 'Lisa', 'Thompson', 'lisa.thompson@techcorp.com', 'lisa.thompson', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'TECHNICIAN', 'ACTIVE', '2024-01-04 10:20:00', '2024-01-04 14:45:00', '2024-01-04 14:45:00', false, 'lisa-thompson-keycloak-id'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'lisa.thompson@techcorp.com');

INSERT INTO users (first_name, last_name, email, username, password, role, status, created_at, updated_at, approval_date, is_first_login, keycloak_user_id) 
SELECT 'James', 'Wilson', 'james.wilson@techcorp.com', 'james.wilson', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'TECHNICIAN', 'ACTIVE', '2024-01-05 09:30:00', '2024-01-05 16:00:00', '2024-01-05 16:00:00', false, 'james-wilson-keycloak-id'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'james.wilson@techcorp.com');

INSERT INTO users (first_name, last_name, email, username, password, role, status, created_at, updated_at, approval_date, is_first_login, keycloak_user_id) 
SELECT 'Emma', 'Garcia', 'emma.garcia@techcorp.com', 'emma.garcia', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'TECHNICIAN', 'ACTIVE', '2024-01-06 11:00:00', '2024-01-06 15:30:00', '2024-01-06 15:30:00', false, 'emma-garcia-keycloak-id'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'emma.garcia@techcorp.com');

-- New Technician Pending Approval
INSERT INTO users (first_name, last_name, email, username, password, role, status, created_at, updated_at, is_first_login) 
SELECT 'Robert', 'Kim', 'robert.kim@techcorp.com', 'robert.kim', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'TECHNICIAN', 'PENDING', '2024-01-20 14:30:00', '2024-01-20 14:30:00', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'robert.kim@techcorp.com');

-- =====================================================
-- EMPLOYEES (Company Staff)  
-- =====================================================
-- Marketing Department
INSERT INTO users (first_name, last_name, email, username, password, role, status, created_at, updated_at, approval_date, is_first_login, keycloak_user_id) 
SELECT * FROM (VALUES
    ('Amanda', 'Peterson', 'amanda.peterson@techcorp.com', 'amanda.peterson', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'EMPLOYEE', 'ACTIVE', '2024-01-08 09:00:00', '2024-01-08 12:15:00', '2024-01-08 12:15:00', false, 'amanda-peterson-keycloak-id'),
    ('Mark', 'Anderson', 'mark.anderson@techcorp.com', 'mark.anderson', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'EMPLOYEE', 'ACTIVE', '2024-01-09 10:30:00', '2024-01-09 14:20:00', '2024-01-09 14:20:00', false, 'mark-anderson-keycloak-id'),
    ('Jennifer', 'Lee', 'jennifer.lee@techcorp.com', 'jennifer.lee', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'EMPLOYEE', 'ACTIVE', '2024-01-10 08:15:00', '2024-01-10 11:45:00', '2024-01-10 11:45:00', false, 'jennifer-lee-keycloak-id'),
-- Sales Department
    ('Christopher', 'Martinez', 'christopher.martinez@techcorp.com', 'christopher.martinez', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'EMPLOYEE', 'ACTIVE', '2024-01-11 09:45:00', '2024-01-11 13:30:00', '2024-01-11 13:30:00', false, 'christopher-martinez-keycloak-id'),
    ('Rachel', 'Brown', 'rachel.brown@techcorp.com', 'rachel.brown', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'EMPLOYEE', 'ACTIVE', '2024-01-12 10:20:00', '2024-01-12 15:10:00', '2024-01-12 15:10:00', false, 'rachel-brown-keycloak-id'),
    ('Steven', 'Davis', 'steven.davis@techcorp.com', 'steven.davis', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'EMPLOYEE', 'ACTIVE', '2024-01-13 11:30:00', '2024-01-13 16:00:00', '2024-01-13 16:00:00', false, 'steven-davis-keycloak-id'),
-- Finance Department
    ('Michelle', 'Taylor', 'michelle.taylor@techcorp.com', 'michelle.taylor', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'EMPLOYEE', 'ACTIVE', '2024-01-14 08:30:00', '2024-01-14 12:45:00', '2024-01-14 12:45:00', false, 'michelle-taylor-keycloak-id'),
    ('Kevin', 'White', 'kevin.white@techcorp.com', 'kevin.white', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'EMPLOYEE', 'ACTIVE', '2024-01-15 09:15:00', '2024-01-15 13:20:00', '2024-01-15 13:20:00', false, 'kevin-white-keycloak-id'),
    ('Laura', 'Jackson', 'laura.jackson@techcorp.com', 'laura.jackson', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'EMPLOYEE', 'ACTIVE', '2024-01-16 10:00:00', '2024-01-16 14:30:00', '2024-01-16 14:30:00', false, 'laura-jackson-keycloak-id'),
-- HR Department
    ('Daniel', 'Miller', 'daniel.miller@techcorp.com', 'daniel.miller', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'EMPLOYEE', 'ACTIVE', '2024-01-17 08:45:00', '2024-01-17 12:00:00', '2024-01-17 12:00:00', false, 'daniel-miller-keycloak-id'),
    ('Nicole', 'Moore', 'nicole.moore@techcorp.com', 'nicole.moore', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'EMPLOYEE', 'ACTIVE', '2024-01-18 09:30:00', '2024-01-18 13:45:00', '2024-01-18 13:45:00', false, 'nicole-moore-keycloak-id')
) AS new_users(first_name, last_name, email, username, password, role, status, created_at, updated_at, approval_date, is_first_login, keycloak_user_id)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE users.email = new_users.email);

-- =====================================================
-- PENDING EMPLOYEES (Awaiting Approval)
-- =====================================================
-- Recent signups waiting for admin approval
INSERT INTO users (first_name, last_name, email, username, password, role, status, created_at, updated_at, is_first_login) 
SELECT * FROM (VALUES
    ('Alex', 'Thompson', 'alex.thompson@techcorp.com', 'alex.thompson', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'EMPLOYEE', 'PENDING', '2024-01-21 10:15:00', '2024-01-21 10:15:00', true),
    ('Samantha', 'Clark', 'samantha.clark@techcorp.com', 'samantha.clark', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'EMPLOYEE', 'PENDING', '2024-01-21 14:20:00', '2024-01-21 14:20:00', true),
    ('Brian', 'Lewis', 'brian.lewis@techcorp.com', 'brian.lewis', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'TECHNICIAN', 'PENDING', '2024-01-22 09:45:00', '2024-01-22 09:45:00', true),
    ('Catherine', 'Hall', 'catherine.hall@techcorp.com', 'catherine.hall', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'EMPLOYEE', 'PENDING', '2024-01-22 15:30:00', '2024-01-22 15:30:00', true)
) AS new_pending(first_name, last_name, email, username, password, role, status, created_at, updated_at, is_first_login)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE users.email = new_pending.email);

-- =====================================================
-- INACTIVE USERS (Deactivated Accounts)
-- =====================================================
-- Former employees or temporarily deactivated accounts
INSERT INTO users (first_name, last_name, email, username, password, role, status, created_at, updated_at, approval_date, is_first_login, keycloak_user_id) VALUES
('Richard', 'Young', 'richard.young@techcorp.com', 'richard.young', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'EMPLOYEE', 'INACTIVE', '2023-12-01 09:00:00', '2024-01-15 16:30:00', '2023-12-01 12:00:00', false, 'richard-young-keycloak-id'),
('Patricia', 'Allen', 'patricia.allen@techcorp.com', 'patricia.allen', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'TECHNICIAN', 'INACTIVE', '2023-11-15 10:30:00', '2024-01-10 14:20:00', '2023-11-15 14:00:00', false, 'patricia-allen-keycloak-id');

-- =====================================================
-- REJECTED USERS (Applications Declined)
-- =====================================================
-- Examples of rejected applications for security reasons
INSERT INTO users (first_name, last_name, email, username, password, role, status, created_at, updated_at, rejection_reason, is_first_login) VALUES
('Unknown', 'User', 'suspicious.email@external.com', 'unknown.user', '$2a$10$8K2L0Hkd1Pz5cnm/RbU4Oe6IUG3I6Q7wN3E9d8F2G4H6J8K0L2N4', 'EMPLOYEE', 'REJECTED', '2024-01-19 11:30:00', '2024-01-19 15:45:00', 'Non-company email domain - security policy violation', true);
