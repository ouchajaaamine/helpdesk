# 🧪 **API Testing Guide with Sample Data**

## 📊 **Sample Data Overview**

Your database now contains **18 sample users** with realistic data:

### 👥 **User Distribution:**
- **2 ADMIN users** (John Admin, Sarah Manager)
- **3 TECHNICIAN users** (Mike, Lisa, David)
- **8 ACTIVE EMPLOYEE users** (Alice, Bob, Carol, Daniel, Emma, Frank, Grace, Henry)
- **3 PENDING EMPLOYEE users** (Ian, Julia, Kevin) - *waiting for approval*
- **2 INACTIVE users** (Laura, Mark)

### 🔑 **Default Password for All Users:**
```
password123
```
*(BCrypt hashed in the database)*

---

## 🧪 **Quick API Tests**

### **1. Start the Application**
```bash
cd backend/user-service
mvn spring-boot:run
```

### **2. Access Swagger UI**
```
http://localhost:8081/swagger-ui.html
```

### **3. Test Scenarios**

#### **✅ Test User Registration**
```bash
curl -X POST http://localhost:8081/api/users/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User",
    "email": "test.user@company.com"
  }'
```

#### **✅ Test Admin Approval (Approve Ian Taylor)**
```bash
curl -X POST http://localhost:8081/api/admin/pending-users/9/approve
```

#### **✅ Test User Statistics**
```bash
curl http://localhost:8081/api/admin/stats
```
*Expected response:*
```json
{
  "totalUsers": 18,
  "pendingUsers": 3,
  "activeUsers": 13,
  "adminCount": 2,
  "technicianCount": 3,
  "employeeCount": 13
}
```

#### **✅ Test User Search**
```bash
curl "http://localhost:8081/api/admin/users/search?query=john"
```
*Should return: John Admin*

#### **✅ Test Role Filtering**
```bash
curl http://localhost:8081/api/admin/users/role/ADMIN
```
*Should return: John Admin, Sarah Manager*

#### **✅ Test Profile Update**
```bash
curl -X PUT http://localhost:8081/api/users/1/profile \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Administrator",
    "email": "john.admin@helpdesk.com"
  }'
```

---

## 🎯 **Key Test Cases**

### **Admin Dashboard Tests:**
1. **View all users**: `GET /api/admin/users`
2. **View pending users**: `GET /api/admin/pending-users`
3. **Approve user**: `POST /api/admin/pending-users/{id}/approve`
4. **View statistics**: `GET /api/admin/stats`

### **User Management Tests:**
1. **Search users**: `GET /api/admin/users/search?query={name}`
2. **Filter by role**: `GET /api/admin/users/role/{role}`
3. **Change user role**: `PUT /api/admin/users/{id}/role?role=TECHNICIAN`
4. **Deactivate user**: `PUT /api/admin/users/{id}/deactivate`

### **Profile Management Tests:**
1. **Get user profile**: `GET /api/users/{id}`
2. **Update profile**: `PUT /api/users/{id}/profile`
3. **Change password**: `PUT /api/users/{id}/password`

---

## 📋 **Sample User IDs for Testing**

| ID | Name | Role | Status | Email |
|----|------|------|--------|-------|
| 1 | John Admin | ADMIN | ACTIVE | john.admin@helpdesk.com |
| 2 | Sarah Manager | ADMIN | ACTIVE | sarah.manager@helpdesk.com |
| 3 | Mike Technician | TECHNICIAN | ACTIVE | mike.tech@helpdesk.com |
| 9 | Ian Taylor | EMPLOYEE | PENDING | ian.taylor@company.com |
| 10 | Julia Anderson | EMPLOYEE | PENDING | julia.anderson@company.com |
| 11 | Kevin Thomas | EMPLOYEE | PENDING | kevin.thomas@company.com |
| 16 | Laura Jackson | EMPLOYEE | INACTIVE | laura.jackson@company.com |
| 17 | Mark White | TECHNICIAN | INACTIVE | mark.white@company.com |

---

## 🚀 **Frontend Integration Ready**

Your API is now ready for frontend integration with:
- ✅ **18 test users** with realistic data
- ✅ **Complete CRUD operations** working
- ✅ **Role-based access** implemented
- ✅ **Interactive Swagger UI** for testing
- ✅ **Comprehensive documentation**

**Start your application and test the API endpoints!** 🎉
