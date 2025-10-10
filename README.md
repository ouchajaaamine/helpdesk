# Helpdesk Microservices Application

A comprehensive microservices-based helpdesk system built with Spring Boot, featuring complete CI/CD pipeline integration and monitoring capabilities.

## 🏗️ Architecture

### Microservices Stack
- **Discovery Service** - Eureka service registry for service discovery
- **Gateway Service** - API Gateway with routing and load balancing  
- **User Service** - User management and authentication
- **Ticket Service** - Helpdesk ticket management system

### Infrastructure
- **Keycloak** - Identity and access management
- **PostgreSQL** - Database (separate instances for User and Ticket services)
- **Prometheus** - Metrics collection and monitoring
- **Grafana** - Visualization and dashboards

## 🚀 Deployment Options

### 1. Azure Production Deployment
- **Target**: Azure VM with Docker Compose
- **Pipeline**: Automated via GitLab CI/CD
- **Branch**: `main`
- **Access**: Production environment

### 2. Kubernetes Local Development  
- **Target**: Minikube cluster
- **Pipeline**: Automated via GitLab CI/CD
- **Branch**: `k8s-local`
- **Monitoring**: Prometheus + Grafana included

## 📊 Monitoring & Observability

### Monitoring Stack (Kubernetes)
The application includes a complete monitoring solution:

- **Prometheus** (`http://localhost:30090`)
  - Collects metrics from all microservices
  - Service discovery for automatic target detection
  - Self-monitoring and alerting capabilities

- **Grafana** (`http://localhost:30300`)
  - Pre-configured Prometheus data source
  - Login: `admin` / `admin123`
  - Ready for custom dashboard creation

### Metrics Collection
All Spring Boot services expose Prometheus metrics at `/actuator/prometheus`:
- **Application metrics**: JVM, HTTP requests, database connections
- **Business metrics**: Custom metrics per service
- **Infrastructure metrics**: Container and cluster resources

## 🔄 CI/CD Pipeline

### Pipeline Stages
1. **build** - Maven compilation and JAR creation
2. **test** - Unit tests + SonarQube quality analysis  
3. **docker_build** - Docker image creation
4. **security_scan** - Trivy vulnerability scanning
5. **docker_push** - GitLab Registry image push
6. **deploy** - Environment deployment (Azure VM / Kubernetes)

### Monitoring CI/CD Integration
- **Automated Deployment**: Monitoring stack deploys after main services
- **Health Checks**: Verifies all targets are UP before completion
- **Environment**: `k8s-monitoring` with Grafana access URL
- **Manual Trigger**: Safe deployment with failure tolerance

## 📁 Project Structure

```
helpdesk/
├── backend/                          # Microservices source code
│   ├── discovery-service/           # Eureka service registry
│   ├── gateway-service/             # API Gateway
│   ├── user-service/                # User management
│   └── ticket-service/              # Ticket system
├── kubernetes/                       # K8s deployment manifests
│   ├── *-service.yaml              # Application services
│   ├── prometheus-*.yaml           # Prometheus monitoring
│   └── grafana-*.yaml              # Grafana dashboards
├── terraform/                       # Infrastructure as Code
├── keycloak-import/                 # Keycloak realm configuration
├── .gitlab-ci.yml                  # Complete CI/CD pipeline
└── docker-compose.*.yml            # Container orchestration
```

## 🛠️ Quick Start

### Prerequisites
- Java 11+
- Maven 3.6+
- Docker & Docker Compose
- Kubernetes (Minikube for local)
- GitLab account with CI/CD enabled

### Local Development (Kubernetes)
```bash
# 1. Clone repository
git clone https://gitlab.com/ouchajaaamine/helpdesk.git
cd helpdesk

# 2. Start Minikube
minikube start

# 3. Deploy via GitLab CI/CD
# Push to k8s-local branch and trigger pipeline manually

# 4. Access services
kubectl get services
```

### Local Development (Docker Compose)
```bash
# 1. Build services
docker-compose build

# 2. Start all services
docker-compose up -d

# 3. Access applications
# Gateway: http://localhost:8080
# Keycloak: http://localhost:8180
```

## 🔍 Service Access Points

### Production (Azure VM)
- **API Gateway**: `http://{AZURE_VM_IP}:8080`
- **Keycloak Admin**: `http://{AZURE_VM_IP}:8180`
- **Eureka Dashboard**: `http://{AZURE_VM_IP}:8761`

### Kubernetes (Local)
- **API Gateway**: `http://localhost:30080`  
- **Keycloak Admin**: `http://localhost:30180`
- **Eureka Dashboard**: `http://localhost:30761`
- **Prometheus**: `http://localhost:30090`
- **Grafana**: `http://localhost:30300`

## 📚 Documentation

- **[Deployment Guide](DEPLOYMENT-GUIDE.md)** - Complete deployment instructions
- **[K8s Integration](K8S-INTEGRATION-SUMMARY.md)** - Kubernetes setup details
- **[Monitoring CI/CD](MONITORING-CICD-GUIDE.md)** - Monitoring pipeline integration
- **[Local Runner Setup](LOCAL-RUNNER-SETUP.md)** - GitLab Runner configuration

## 🔐 Security Features

- **Vulnerability Scanning**: Trivy security analysis in CI/CD
- **Registry Security**: Authenticated GitLab Registry access
- **Authentication**: Keycloak SSO integration
- **Network Security**: Service mesh communication
- **RBAC**: Kubernetes role-based access control

## 🏃‍♂️ Pipeline Usage

### Trigger Monitoring Deployment
1. Push code to `k8s-local` or `monitoring` branch
2. Go to GitLab CI/CD > Pipelines
3. Click "Run Pipeline" 
4. Manually trigger `deploy-to-k8s-local`
5. Manually trigger `deploy-monitoring-k8s`
6. Access Grafana at `http://localhost:30300`

### Pipeline Status
- **Build Status**: [![pipeline status](https://gitlab.com/ouchajaaamine/helpdesk/badges/main/pipeline.svg)](https://gitlab.com/ouchajaaamine/helpdesk/-/commits/main)
- **Coverage**: Automated testing with SonarQube integration
- **Security**: Trivy scanning prevents vulnerable image deployment

## 📈 Monitoring Dashboards

After deployment, create Grafana dashboards for:
- **Application Performance**: Request rates, response times, error rates
- **Infrastructure Metrics**: CPU, memory, disk usage
- **Business Metrics**: User registrations, ticket creation rates
- **Service Health**: Service availability and dependency mapping

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Merge Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👨‍💻 Authors

- **[Your Name]** - Initial work and architecture
- **Contributors** - See GitLab contributors page

## 🙏 Acknowledgments

- Spring Boot team for excellent microservices framework
- Prometheus & Grafana for monitoring capabilities  
- GitLab for comprehensive CI/CD platform
- Kubernetes community for container orchestration

---

For detailed setup instructions, see the [DEPLOYMENT-GUIDE.md](DEPLOYMENT-GUIDE.md)
