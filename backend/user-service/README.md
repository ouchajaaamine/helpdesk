# HelpDesk User Service API Documentation

## Overview
The User Service is a comprehensive user management microservice for the HelpDesk ticketing system. It provides complete user lifecycle management with role-based access control.

## 🚀 Quick Start

### Access Swagger UI
Once the application is running, access the interactive API documentation at:
```
http://localhost:8081/swagger-ui.html
```

### API Base URL
```
http://localhost:8081/api
```

## 📋 API Endpoints

### 🔐 Authentication & Registration
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/users/signup` | Register new user (creates PENDING status) |

### 👨‍💼 Admin Operations
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/admin/pending-users` | Get all pending users |
| `GET` | `/api/admin/pending-users/{id}` | Get specific pending user |
| `POST` | `/api/admin/pending-users/{id}/approve` | Approve user registration |
| `POST` | `/api/admin/users/{id}/reject` | Reject user registration |
| `GET` | `/api/admin/users` | Get all users |
| `GET` | `/api/admin/users/active` | Get active users only |
| `GET` | `/api/admin/users/{id}` | Get user by ID |
| `PUT` | `/api/admin/users/{id}/role` | Change user role |
| `PUT` | `/api/admin/users/{id}/deactivate` | Deactivate user |
| `GET` | `/api/admin/stats` | Get user statistics |
| `GET` | `/api/admin/users/search?q={query}` | Search users |
| `GET` | `/api/admin/users/role/{role}` | Get users by role |

### 👤 User Profile Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/users/{id}` | Get user profile |
| `PUT` | `/api/users/{id}/profile` | Update user profile |
| `PUT` | `/api/users/{id}/password` | Change password |

## 🎭 User Roles

### ADMIN
- ✅ Approve/reject user registrations
- ✅ Manage all user accounts
- ✅ Change user roles
- ✅ View system statistics
- ✅ Search and filter users

### TECHNICIAN
- ✅ View assigned tickets (future feature)
- ✅ Update ticket status (future feature)
- ✅ Access user information for support

### EMPLOYEE
- ✅ Register for account
- ✅ Update own profile
- ✅ Create tickets (future feature)
- ✅ View own tickets (future feature)

## 🔄 User Lifecycle

```
1. REGISTRATION → User submits signup form
   ↓
2. PENDING → Admin reviews registration
   ↓
3. APPROVED/REJECTED → Admin decision
   ↓
4. ACTIVE → User can access system
   ↓
5. INACTIVE → Account deactivated (optional)
```

## 📊 Data Models

### User Object
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "username": "john.doe",
  "email": "john.doe@example.com",
  "role": "EMPLOYEE",
  "status": "ACTIVE",
  "createdAt": "2024-01-15T10:30:00",
  "approvalDate": "2024-01-15T14:20:00",
  "isFirstLogin": false
}
```

### User Statistics
```json
{
  "totalUsers": 150,
  "pendingUsers": 5,
  "activeUsers": 140,
  "adminCount": 3,
  "technicianCount": 20,
  "employeeCount": 117
}
```

## 🔧 Configuration

### Database
- **Type**: PostgreSQL
- **Host**: localhost:5432
- **Database**: userdb
- **Username**: postgres
- **Password**: password

### Email (Optional)
- **Host**: smtp.gmail.com
- **Port**: 587
- **Username**: your-email@gmail.com
- **Password**: your-app-password

## 🧪 Testing the API

### 1. Start the Service
```bash
cd backend/user-service
mvn spring-boot:run
```

### 2. Access Swagger UI
Open: `http://localhost:8081/swagger-ui.html`

### 3. Test User Registration
```bash
curl -X POST http://localhost:8081/api/users/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com"
  }'
```

### 4. Admin Approval
```bash
curl -X POST http://localhost:8081/api/admin/pending-users/1/approve
```

## 📈 Monitoring & Analytics

### User Statistics Endpoint
```bash
curl http://localhost:8081/api/admin/stats
```

Returns real-time statistics for dashboard integration.

## 🔒 Security Features

- **Password Encryption**: BCrypt hashing
- **Role-Based Access**: Method-level security
- **Input Validation**: Comprehensive validation
- **CORS Support**: Frontend integration ready
- **Audit Trail**: JPA timestamps for all operations

## 🚀 Production Deployment

### Environment Variables
```bash
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://prod-host:5432/userdb
DB_USERNAME=prod-user
DB_PASSWORD=prod-password
MAIL_USERNAME=prod-email@company.com
MAIL_PASSWORD=prod-email-password
```

### Health Checks
- **Application Health**: `http://localhost:8081/actuator/health`
- **API Documentation**: `http://localhost:8081/api-docs`

## 📞 Support

For API support or questions:
- **Swagger UI**: Interactive documentation
- **API Docs**: OpenAPI 3.0 specification
- **Error Codes**: Detailed error responses with HTTP status codes

---

**🎯 This User Service provides a solid foundation for your HelpDesk system with comprehensive user management capabilities!**
