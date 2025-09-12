# Ticket Service - Authentication & User Context

## 🔐 How User Authentication Works

### **1. JWT Token Flow**
```
Client Request → JWT Token in Header → JwtAuthenticationFilter → SecurityContext → Service Methods
```

### **2. User Context Extraction**
When a user creates a ticket, the system automatically:
- ✅ Extracts user ID from JWT token
- ✅ Validates user permissions
- ✅ Sets `createdById` automatically
- ✅ Logs the action with user context

### **3. API Usage**

#### **Creating a Ticket (Automatic User Context)**
```bash
POST /api/tickets
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "title": "Computer not starting",
  "description": "My computer won't turn on",
  "priority": "HIGH",
  "category": "HARDWARE"
}

# System automatically sets:
# - createdById: extracted from JWT
# - createdAt: current timestamp
# - status: OPEN
# - SLA deadline: calculated based on priority
```

#### **JWT Token Structure**
```json
{
  "sub": "john.admin@helpdesk.com",
  "userId": "1",
  "role": "ADMIN",
  "iat": 1640995200,
  "exp": 1641081600
}
```

### **4. Role-Based Access Control**

#### **Employee**
- ✅ Create tickets
- ✅ View their own tickets
- ✅ Update their own tickets

#### **Technician**
- ✅ All employee permissions
- ✅ View assigned tickets
- ✅ Update ticket status
- ✅ Add comments

#### **Admin**
- ✅ All permissions
- ✅ View all tickets
- ✅ Assign tickets to technicians
- ✅ Generate reports

### **5. Security Implementation**

#### **Authentication Filter**
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // Extracts JWT from Authorization header
    // Validates token signature
    // Sets SecurityContext with user details
}
```

#### **User Details Service**
```java
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    // Loads user by username
    // Returns UserDetails with authorities
}
```

### **6. Testing the Authentication**

#### **1. Get JWT Token**
```bash
# Login to get token (you'd implement this in user-service)
curl -X POST http://localhost:8081/api/auth/login \
  -d '{"username":"john.admin@helpdesk.com","password":"password"}'
```

#### **2. Use Token to Create Ticket**
```bash
curl -X POST http://localhost:8082/api/tickets \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test ticket",
    "description": "Testing authentication",
    "priority": "MEDIUM",
    "category": "SOFTWARE"
  }'
```

### **7. What Happens Automatically**

#### **When User Creates Ticket:**
1. **JWT Validation**: Token is validated
2. **User Extraction**: User ID and role extracted from token
3. **Permission Check**: User must be authenticated
4. **Auto-Assignment**: `createdById` set automatically
5. **Audit Trail**: Action logged with user context

#### **When User Views Tickets:**
1. **Role Check**: Filter tickets based on user role
2. **Ownership Check**: Employees see only their tickets
3. **Assignment Check**: Technicians see assigned tickets

### **8. Integration Points**

#### **With User Service**
- User validation
- Role verification
- User details enrichment

#### **With Gateway**
- Token validation
- Request routing
- Load balancing

### **9. Security Best Practices**

✅ **JWT Tokens**: Stateless authentication
✅ **Role-Based Access**: Granular permissions
✅ **Audit Logging**: Track all actions
✅ **Input Validation**: Sanitize all inputs
✅ **CORS Configuration**: Proper cross-origin setup

### **10. Next Steps**

1. **Implement JWT generation** in user-service
2. **Add refresh token support**
3. **Implement logout functionality**
4. **Add rate limiting**
5. **Set up proper CORS**

---

## 🎯 **Key Takeaway**

**The system automatically knows who created a ticket because:**
- JWT tokens contain user identity
- Authentication filter extracts user context
- Service methods use SecurityContext
- Database stores user relationships
- Authorization validates permissions

**No manual user ID passing required!** 🚀
