# 🚀 PRODUCTION DEPLOYMENT GUIDE

## 📁 Files for VM Deployment

### Required Files to Transfer to VM:
```
Transfer to VM (/home/azureuser/helpdesk/):
├── ✅ docker-compose.prod.yml (main production compose)
├── ✅ setup-keycloak-realm.sh (realm setup script)
└── ✅ keycloak-import/ directory (if exists locally)
```

## 🔐 Authentication Setup

### 1. GitLab Registry Login on VM:
```bash
# SSH into VM first
ssh azureuser@4.233.111.97

# Login to GitLab Registry
docker login registry.gitlab.com
# Username: ouchajaaamine
# Password: [Your GitLab Personal Access Token]
```

### 2. Create GitLab Personal Access Token:
```
GitLab Steps:
1. Go to GitLab → Settings → Access Tokens
2. Create token with scopes: read_registry, read_repository
3. Copy token for docker login
```

## 🚀 Deployment Commands

### 1. Transfer Files to VM:
```bash
# From your local machine (in helpdesk directory)
scp docker-compose.prod.yml azureuser@4.233.111.97:/home/azureuser/helpdesk/
scp setup-keycloak-realm.sh azureuser@4.233.111.97:/home/azureuser/helpdesk/
scp -r keycloak-import azureuser@4.233.111.97:/home/azureuser/helpdesk/ 2>/dev/null || echo "No keycloak-import directory found"
```

### 2. Setup on VM:
```bash
# SSH into VM
ssh azureuser@4.233.111.97

# Navigate to project directory
cd /home/azureuser/helpdesk

# Make setup script executable
chmod +x setup-keycloak-realm.sh

# Run Keycloak setup
./setup-keycloak-realm.sh

# Pull all images from GitLab Registry
docker-compose -f docker-compose.prod.yml pull

# Start all services
docker-compose -f docker-compose.prod.yml up -d
```

### 3. Verify Deployment:
```bash
# Check all containers are running
docker ps

# Check logs for any issues
docker-compose -f docker-compose.prod.yml logs

# Test service health
curl http://4.233.111.97:8080/actuator/health
curl http://4.233.111.97:8180
```

## 📊 Service Access URLs

### External Access:
```
🌐 Gateway (Main API): http://4.233.111.97:8080
🔑 Keycloak Admin: http://4.233.111.97:8180
🔍 Eureka Dashboard: http://4.233.111.97:8761
```

### Internal Services (VM only):
```
👤 User Service: http://localhost:8081
🎫 Ticket Service: http://localhost:8082
🗄️ PostgreSQL User DB: localhost:5432
🗄️ PostgreSQL Ticket DB: localhost:5433
```

## 💾 Data Persistence Strategy

### Persistent Volumes:
```
Data Storage (survives container restarts):
├── 🔑 keycloak-data: All Keycloak configuration and users
├── 📁 keycloak-import: Realm import files
├── 👤 user_db_data: User service database
└── 🎫 ticket_db_data: Ticket service database
```

### Backup Commands:
```bash
# Backup all data volumes
docker run --rm -v keycloak-data:/data -v $(pwd):/backup alpine tar czf /backup/keycloak-backup.tar.gz -C /data .
docker run --rm -v user_db_data:/data -v $(pwd):/backup alpine tar czf /backup/userdb-backup.tar.gz -C /data .
docker run --rm -v ticket_db_data:/data -v $(pwd):/backup alpine tar czf /backup/ticketdb-backup.tar.gz -C /data .
```

## 🔧 Memory Optimization

### Resource Limits Applied:
```
Memory Allocation (Total: ~3.5GB for 4GB VM):
├── 🔑 Keycloak: 900MB (largest, authentication server)
├── 🌐 Gateway: 400MB (routing + discovery client)
├── 👤 User Service: 450MB (database connections)
├── 🎫 Ticket Service: 450MB (database connections)
├── 🔍 Discovery: 350MB (service registry)
├── 🗄️ PostgreSQL x2: 400MB (2x 200MB databases)
└── 💻 System Reserve: 500MB
```

## 🚨 Troubleshooting

### Common Issues:
```
❌ Out of Memory:
   → Check: docker stats
   → Solution: Restart services individually

❌ Registry Auth Failed:
   → Check: docker login registry.gitlab.com
   → Solution: Recreate GitLab Personal Access Token

❌ Keycloak Import Failed:
   → Check: Volume mount and file permissions
   → Solution: Manual import via admin console

❌ Service Health Check Failed:
   → Check: docker-compose logs [service-name]
   → Solution: Check database connections and discovery
```

## ✅ Success Checklist

### Deployment Verification:
```
✅ All 7 containers running (docker ps)
✅ Gateway responds: curl http://4.233.111.97:8080
✅ Keycloak admin: http://4.233.111.97:8180
✅ Eureka shows all services registered
✅ Memory usage < 3.5GB (docker stats)
✅ All health checks passing
✅ Database connections working
✅ Persistent volumes created and mounted
```