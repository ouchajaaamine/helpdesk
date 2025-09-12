-- =============================================================================
-- TechCorp HelpDesk Ticket Service - Sample Data
-- =============================================================================
-- This data works with the User Service IDs for a realistic helpdesk environment

-- =====================================================
-- URGENT TICKETS (High Priority - Need Immediate Attention)
-- =====================================================
INSERT INTO tickets (title, description, status, priority, category, created_by_id, created_at, updated_at, estimated_hours) VALUES
-- Active urgent tickets requiring immediate response
('Server Room Access Card Not Working', 'Cannot access the main server room. System shows my card as invalid. This is blocking critical maintenance scheduled for today.', 'OPEN', 'URGENT', 'ACCESS', 8, '2024-01-22 08:30:00', '2024-01-22 08:30:00', 1),
('Database Connection Failed - Production Down', 'Primary customer database is not responding. All customer services are affected. Error: Connection timeout after 30 seconds.', 'IN_PROGRESS', 'URGENT', 'SOFTWARE', 10, '2024-01-22 07:15:00', '2024-01-22 07:45:00', 4),
('Network Outage - Marketing Department', 'Entire marketing floor has no internet connectivity. Switch appears to have failed. Critical campaign launch is today.', 'OPEN', 'URGENT', 'NETWORK', 9, '2024-01-22 09:00:00', '2024-01-22 09:00:00', 3);

-- =====================================================
-- HIGH PRIORITY TICKETS (Important but not critical)
-- =====================================================
INSERT INTO tickets (title, description, status, priority, category, created_by_id, created_at, updated_at, estimated_hours) VALUES
-- Hardware issues affecting productivity
('Laptop Overheating and Frequent Crashes', 'My work laptop has been overheating and crashing 3-4 times per day. Fan sounds very loud. Affecting client presentations.', 'OPEN', 'HIGH', 'HARDWARE', 12, '2024-01-21 14:20:00', '2024-01-21 14:20:00', 2),
('Shared Printer Queue Stuck', 'Main office printer on Floor 3 is not processing any print jobs. Queue shows 47 pending documents. Error light is blinking.', 'IN_PROGRESS', 'HIGH', 'HARDWARE', 15, '2024-01-21 11:30:00', '2024-01-21 13:15:00', 1),
('Email Server Slow Response Times', 'Email is taking 2-3 minutes to send/receive. Outlook shows frequent disconnections. Multiple users in Sales dept affected.', 'OPEN', 'HIGH', 'SOFTWARE', 13, '2024-01-21 16:45:00', '2024-01-21 16:45:00', 3);

-- =====================================================
-- MEDIUM PRIORITY TICKETS (Standard Issues)
-- =====================================================
INSERT INTO tickets (title, description, status, priority, category, created_by_id, created_at, updated_at, estimated_hours) VALUES
-- Common software and hardware requests
('Microsoft Teams Audio Issues', 'Cannot hear other participants in Teams meetings. Audio works fine in other applications. Tried restarting multiple times.', 'OPEN', 'MEDIUM', 'SOFTWARE', 11, '2024-01-20 10:15:00', '2024-01-20 10:15:00', 1),
('Request for Additional Monitor', 'Would like to request a second monitor for my workstation to improve productivity. Current setup has space for dual monitors.', 'OPEN', 'MEDIUM', 'HARDWARE', 14, '2024-01-20 13:45:00', '2024-01-20 13:45:00', 1),
('WiFi Connection Drops Frequently', 'WiFi connection drops every 15-20 minutes in Conference Room B. Have to reconnect manually each time during meetings.', 'IN_PROGRESS', 'MEDIUM', 'NETWORK', 16, '2024-01-19 15:30:00', '2024-01-20 09:00:00', 2),
('Software Installation Request - Adobe Creative Suite', 'Need Adobe Creative Suite installed for new marketing campaign work. Have valid license from procurement department.', 'OPEN', 'MEDIUM', 'SOFTWARE', 9, '2024-01-19 11:20:00', '2024-01-19 11:20:00', 2);

-- =====================================================
-- RESOLVED TICKETS (Recently Completed)
-- =====================================================
INSERT INTO tickets (title, description, status, priority, category, created_by_id, created_at, updated_at, estimated_hours, assigned_to_id, resolved_at, resolution, actual_resolution_time) VALUES
-- Successfully resolved issues with solutions
('Password Reset - Unable to Login', 'Forgot password after vacation. Account appears to be locked after multiple failed attempts.', 'RESOLVED', 'HIGH', 'ACCESS', 17, '2024-01-18 09:00:00', '2024-01-18 10:30:00', 1, 2, '2024-01-18 10:30:00', 'Password reset completed and account unlocked. User can now login successfully. Recommended enabling 2FA for additional security.', 5400000),
('New Employee Laptop Setup', 'Need laptop configured for new hire starting Monday. Requires standard software package and domain access.', 'RESOLVED', 'MEDIUM', 'HARDWARE', 1, '2024-01-17 14:00:00', '2024-01-18 16:00:00', 3, 4, '2024-01-18 16:00:00', 'Laptop fully configured with Windows 11, Office 365, VPN client, and antivirus. Domain account created and tested. Ready for new employee.', 93600000),
('Outlook Not Syncing Emails', 'Emails not updating since yesterday. Can send but not receiving new messages. Other email clients work fine.', 'RESOLVED', 'MEDIUM', 'SOFTWARE', 18, '2024-01-16 11:30:00', '2024-01-17 09:15:00', 2, 3, '2024-01-17 09:15:00', 'Outlook profile was corrupted. Created new profile and migrated all data. Email sync is now working normally. Provided prevention tips to user.', 77700000);

-- =====================================================
-- CLOSED TICKETS (Completed and Verified)
-- =====================================================
INSERT INTO tickets (title, description, status, priority, category, created_by_id, created_at, updated_at, estimated_hours, assigned_to_id, resolved_at, closed_at, resolution, actual_resolution_time) VALUES
-- Fully completed tickets with user verification
('Security Camera Feed Not Working', 'Security camera in parking lot shows black screen. Need to verify if hardware issue or software problem.', 'CLOSED', 'MEDIUM', 'HARDWARE', 19, '2024-01-15 08:45:00', '2024-01-16 14:20:00', 2, 5, '2024-01-16 11:30:00', '2024-01-16 14:20:00', 'Camera lens was dirty and cable connection was loose. Cleaned lens and secured all connections. Feed is now clear and stable. Scheduled monthly maintenance check.', 97800000),
('VPN Connection Issues from Home', 'Cannot connect to company VPN from home office. Error message: Authentication failed. Was working fine last week.', 'CLOSED', 'HIGH', 'NETWORK', 20, '2024-01-12 19:30:00', '2024-01-15 10:45:00', 2, 2, '2024-01-13 14:15:00', '2024-01-15 10:45:00', 'VPN certificate had expired. Updated certificate and provided user with new configuration file. Connection tested successfully from multiple locations.', 67500000);

-- =====================================================
-- LOW PRIORITY TICKETS (Feature Requests & Minor Issues)
-- =====================================================
INSERT INTO tickets (title, description, status, priority, category, created_by_id, created_at, updated_at, estimated_hours) VALUES
-- Non-urgent requests and minor improvements
('Request for Ergonomic Keyboard', 'Would like to request an ergonomic keyboard for health reasons. Current keyboard causing wrist strain during long work sessions.', 'OPEN', 'LOW', 'HARDWARE', 21, '2024-01-18 16:20:00', '2024-01-18 16:20:00', 1),
('Desktop Shortcut for Company Portal', 'Would like a desktop shortcut created for the company intranet portal. Currently have to type the full URL each time.', 'OPEN', 'LOW', 'SOFTWARE', 22, '2024-01-17 12:00:00', '2024-01-17 12:00:00', 1);

-- =====================================================
-- UPDATE TICKET ASSIGNMENTS AND STATUS CHANGES
-- =====================================================
-- Assign tickets to appropriate technicians
UPDATE tickets SET assigned_to_id = 2, status = 'IN_PROGRESS' WHERE id = 2; -- Database issue to Senior Tech Sarah
UPDATE tickets SET assigned_to_id = 3, status = 'IN_PROGRESS' WHERE id = 5; -- Printer to David Chen
UPDATE tickets SET assigned_to_id = 4, status = 'IN_PROGRESS' WHERE id = 10; -- WiFi to James Wilson

-- =====================================================
-- SET SLA DEADLINES BASED ON PRIORITY
-- =====================================================
-- Urgent: 2 hours, High: 8 hours, Medium: 24 hours, Low: 72 hours
UPDATE tickets SET sla_deadline = created_at + INTERVAL '2 hours' WHERE priority = 'URGENT';
UPDATE tickets SET sla_deadline = created_at + INTERVAL '8 hours' WHERE priority = 'HIGH';
UPDATE tickets SET sla_deadline = created_at + INTERVAL '24 hours' WHERE priority = 'MEDIUM';
UPDATE tickets SET sla_deadline = created_at + INTERVAL '72 hours' WHERE priority = 'LOW';

-- =====================================================
-- MARK OVERDUE TICKETS
-- =====================================================
UPDATE tickets SET is_overdue = true WHERE sla_deadline < CURRENT_TIMESTAMP AND status NOT IN ('RESOLVED', 'CLOSED');

-- =====================================================
-- COMPREHENSIVE AUDIT TRAIL DATA
-- =====================================================
INSERT INTO ticket_audit (ticket_id, action, field_name, old_value, new_value, changed_by, changed_by_name, change_reason, changed_at) VALUES
-- Audit trail for Database Connection ticket
(2, 'CREATE', NULL, NULL, 'Critical database connectivity issue reported', 10, 'Michelle Taylor', 'Initial ticket creation', '2024-01-22 07:15:00'),
(2, 'UPDATE', 'priority', 'HIGH', 'URGENT', 10, 'Michelle Taylor', 'Escalated due to production impact', '2024-01-22 07:20:00'),
(2, 'ASSIGN', 'assignedToId', NULL, '2 (Sarah Johnson)', 1, 'Michael Rodriguez', 'Assigned to senior technician', '2024-01-22 07:30:00'),
(2, 'UPDATE', 'status', 'OPEN', 'IN_PROGRESS', 2, 'Sarah Johnson', 'Investigation started', '2024-01-22 07:45:00'),

-- Audit trail for Laptop Setup (resolved ticket)
(13, 'CREATE', NULL, NULL, 'New employee laptop setup requested', 1, 'Michael Rodriguez', 'Initial ticket creation', '2024-01-17 14:00:00'),
(13, 'ASSIGN', 'assignedToId', NULL, '4 (James Wilson)', 1, 'Michael Rodriguez', 'Assigned to hardware specialist', '2024-01-17 14:15:00'),
(13, 'UPDATE', 'status', 'OPEN', 'IN_PROGRESS', 4, 'James Wilson', 'Started laptop configuration', '2024-01-18 08:30:00'),
(13, 'UPDATE', 'status', 'IN_PROGRESS', 'RESOLVED', 4, 'James Wilson', 'Setup completed successfully', '2024-01-18 16:00:00'),
(13, 'UPDATE', 'resolution', NULL, 'Laptop fully configured with all required software and security settings', 4, 'James Wilson', 'Added resolution details', '2024-01-18 16:00:00'),

-- Audit trail for Printer Queue ticket
(5, 'CREATE', NULL, NULL, 'Shared printer queue malfunction reported', 15, 'Rachel Brown', 'Initial ticket creation', '2024-01-21 11:30:00'),
(5, 'ASSIGN', 'assignedToId', NULL, '3 (David Chen)', 3, 'David Chen', 'Self-assigned for immediate response', '2024-01-21 11:45:00'),
(5, 'UPDATE', 'status', 'OPEN', 'IN_PROGRESS', 3, 'David Chen', 'Investigating printer firmware', '2024-01-21 13:15:00'),

-- Audit trail for VPN Connection (closed ticket)
(16, 'CREATE', NULL, NULL, 'VPN authentication failure from remote location', 20, 'Nicole Moore', 'Initial ticket creation', '2024-01-12 19:30:00'),
(16, 'ASSIGN', 'assignedToId', NULL, '2 (Sarah Johnson)', 1, 'Michael Rodriguez', 'Assigned to network specialist', '2024-01-13 08:00:00'),
(16, 'UPDATE', 'status', 'OPEN', 'IN_PROGRESS', 2, 'Sarah Johnson', 'Investigating certificate issues', '2024-01-13 09:30:00'),
(16, 'UPDATE', 'status', 'IN_PROGRESS', 'RESOLVED', 2, 'Sarah Johnson', 'Certificate updated and tested', '2024-01-13 14:15:00'),
(16, 'UPDATE', 'resolution', NULL, 'VPN certificate renewed and new configuration provided', 2, 'Sarah Johnson', 'Resolution documented', '2024-01-13 14:15:00'),
(16, 'UPDATE', 'status', 'RESOLVED', 'CLOSED', 20, 'Nicole Moore', 'Verified working from home office', '2024-01-15 10:45:00');

-- =====================================================
-- REALISTIC FILE ATTACHMENTS
-- =====================================================
INSERT INTO file_attachments (ticket_id, file_name, file_path, file_size, content_type, uploaded_by, uploaded_by_name, description, uploaded_at) VALUES
-- Server Room Access Card ticket attachments
(1, 'access_card_photo.jpg', 'uploads/tickets/1/f8e7d6c5-b4a3-4291-8765-123456789abc.jpg', 387456, 'image/jpeg', 8, 'Kevin White', 'Photo of access card showing no visible damage', '2024-01-22 08:35:00'),
(1, 'access_log_error.txt', 'uploads/tickets/1/a1b2c3d4-e5f6-7890-1234-567890abcdef.txt', 2048, 'text/plain', 8, 'Kevin White', 'Error log from card reader system', '2024-01-22 08:40:00'),

-- Database Connection Failed ticket attachments
(2, 'database_error_screenshot.png', 'uploads/tickets/2/b2c3d4e5-f6g7-8901-2345-678901bcdefg.png', 156789, 'image/png', 10, 'Michelle Taylor', 'Screenshot of database timeout error', '2024-01-22 07:20:00'),
(2, 'connection_test_results.txt', 'uploads/tickets/2/c3d4e5f6-g7h8-9012-3456-789012cdefgh.txt', 4096, 'text/plain', 2, 'Sarah Johnson', 'Network connectivity test results', '2024-01-22 08:00:00'),
(2, 'database_log_analysis.pdf', 'uploads/tickets/2/d4e5f6g7-h8i9-0123-4567-890123defghi.pdf', 567890, 'application/pdf', 2, 'Sarah Johnson', 'Detailed analysis of database server logs', '2024-01-22 08:30:00'),

-- Laptop Overheating ticket attachments
(4, 'laptop_temperature_reading.jpg', 'uploads/tickets/4/e5f6g7h8-i9j0-1234-5678-901234efghij.jpg', 245760, 'image/jpeg', 12, 'Amanda Peterson', 'HWMonitor screenshot showing CPU temp at 95°C', '2024-01-21 14:25:00'),
(4, 'laptop_fan_noise_recording.mp3', 'uploads/tickets/4/f6g7h8i9-j0k1-2345-6789-012345fghijk.mp3', 1234567, 'audio/mpeg', 12, 'Amanda Peterson', 'Audio recording of unusual fan noise', '2024-01-21 14:30:00'),

-- Printer Queue ticket attachments
(5, 'printer_display_error.jpg', 'uploads/tickets/5/g7h8i9j0-k1l2-3456-7890-123456ghijkl.jpg', 298765, 'image/jpeg', 15, 'Rachel Brown', 'Photo of printer display showing error code E-47', '2024-01-21 11:35:00'),
(5, 'print_queue_screenshot.png', 'uploads/tickets/5/h8i9j0k1-l2m3-4567-8901-234567hijklm.png', 123456, 'image/png', 15, 'Rachel Brown', 'Windows print queue showing 47 stuck jobs', '2024-01-21 11:40:00'),

-- WiFi Connection drops ticket attachments
(10, 'wifi_signal_strength.png', 'uploads/tickets/10/i9j0k1l2-m3n4-5678-9012-345678ijklmn.png', 187654, 'image/png', 16, 'Steven Davis', 'WiFi analyzer showing signal drops every 20 minutes', '2024-01-19 15:45:00'),
(10, 'network_diagnostics.txt', 'uploads/tickets/10/j0k1l2m3-n4o5-6789-0123-456789jklmno.txt', 8192, 'text/plain', 4, 'James Wilson', 'Network diagnostic command outputs', '2024-01-20 09:15:00'),

-- New Employee Laptop Setup ticket attachments
(13, 'software_checklist.pdf', 'uploads/tickets/13/k1l2m3n4-o5p6-7890-1234-567890klmnop.pdf', 345678, 'application/pdf', 4, 'James Wilson', 'Completed software installation checklist', '2024-01-18 15:30:00'),
(13, 'laptop_setup_photos.zip', 'uploads/tickets/13/l2m3n4o5-p6q7-8901-2345-678901lmnopq.zip', 2345678, 'application/zip', 4, 'James Wilson', 'Photos documenting completed setup process', '2024-01-18 16:00:00'),

-- VPN Connection Issues ticket attachments
(16, 'vpn_error_log.txt', 'uploads/tickets/16/m3n4o5p6-q7r8-9012-3456-789012mnopqr.txt', 6144, 'text/plain', 20, 'Nicole Moore', 'VPN client error log from failed connection attempts', '2024-01-12 19:45:00'),
(16, 'network_trace.pcap', 'uploads/tickets/16/n4o5p6q7-r8s9-0123-4567-890123nopqrs.pcap', 987654, 'application/octet-stream', 2, 'Sarah Johnson', 'Network packet capture during VPN connection attempt', '2024-01-13 10:00:00'),
(16, 'new_vpn_config.ovpn', 'uploads/tickets/16/o5p6q7r8-s9t0-1234-5678-901234opqrst.ovpn', 4567, 'application/octet-stream', 2, 'Sarah Johnson', 'Updated VPN configuration file with new certificate', '2024-01-13 14:20:00');
