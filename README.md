# 🎯 Enterprise Helpdesk System - Full DevSecOps Implementation

[![Enterprise Architecture](https://img.shields.io/badge/Architecture-Microservices-blue)](https://gitlab.com/ouchajaaamine/helpdesk)
[![DevSecOps](https://img.shields.io/badge/DevSecOps-Complete-green)](https://gitlab.com/ouchajaaamine/helpdesk)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-Production--Ready-326CE5)](https://gitlab.com/ouchajaaamine/helpdesk)
[![Monitoring](https://img.shields.io/badge/Monitoring-Prometheus%2BGrafana-orange)](https://gitlab.com/ouchajaaamine/helpdesk)
[![Security](https://img.shields.io/badge/Security-Trivy%2BSonarQube-red)](https://gitlab.com/ouchajaaamine/helpdesk)

> **A production-grade enterprise helpdesk system showcasing modern DevSecOps practices, microservices architecture, and complete infrastructure automation with Terraform.**

## 🚀 **Project Overview**

This enterprise-grade helpdesk system demonstrates professional software engineering practices with a complete **DevSecOps pipeline**, **cloud-native architecture**, and **infrastructure automation**. Built as a showcase of modern enterprise development patterns suitable for large-scale production environments.

### 🎨 **System Architecture**

![Architecture Overview](gitlab-pipeline.eraser)

## 🏗️ **Technical Architecture**

### **Core Technologies**
- **Backend**: Spring Boot 3.x, Java 21, Maven
- **Microservices**: Service Discovery (Eureka), API Gateway, Circuit Breakers  
- **Security**: Keycloak SSO, OAuth2/OIDC, Spring Security
- **Databases**: PostgreSQL with separate schemas per service
- **Containerization**: Docker, Multi-stage builds, Distroless images

### **Infrastructure & DevOps**
- **Orchestration**: Kubernetes (Minikube local, AKS production)
- **Cloud**: Microsoft Azure with Terraform automation
- **CI/CD**: GitLab with 6-stage pipeline (Build → Test → Security → Deploy)
- **Monitoring**: Prometheus metrics, Grafana dashboards, Spring Actuator
- **Security**: Trivy vulnerability scanning, SonarQube quality gates

---

## 🎯 **Key Features & Capabilities**

### **🔄 Complete DevSecOps Pipeline**
```yaml
Pipeline Stages:
├── 🔨 Build      → Maven compilation, JAR artifacts
├── 🧪 Test       → JUnit 5, SonarQube quality analysis  
├── 🐳 Docker     → Multi-stage container builds
├── 🛡️ Security   → Trivy CVE scanning, security gates
├── 📤 Registry   → GitLab secure image storage
└── 🚀 Deploy     → Multi-environment automation
```

### **🏢 Enterprise Microservices**
- **Discovery Service** - Eureka service registry with health checks
- **Gateway Service** - API Gateway with load balancing & circuit breakers
- **User Service** - User management with authentication integration
- **Ticket Service** - Core business logic with async processing

### **☁️ Infrastructure Automation**
- **Terraform Azure** - Complete infrastructure provisioning
- **Kubernetes Manifests** - Production-ready deployments
- **Monitoring Stack** - Prometheus + Grafana automation
- **Security Integration** - RBAC, network policies, secrets management

---

## 📊 **Development & Production Environments**

### **🖥️ Development Environment (Kubernetes)**
```bash
Services Access:
├── API Gateway     → http://localhost:30080
├── Keycloak Admin  → http://localhost:30180  
├── Eureka Console  → http://localhost:30761
├── Prometheus      → http://localhost:30090
└── Grafana        → http://localhost:30300
```

### **☁️ Production Environment (Azure)**
- **Azure VM**: Ubuntu 20.04 with Docker Compose orchestration
- **Terraform**: Automated infrastructure provisioning 
- **Security**: Azure Network Security Groups, Key Vault integration
- **Monitoring**: External monitoring and log aggregation

---

## 🛡️ **Security & Quality Assurance**

### **Security Implementation**
- ✅ **Vulnerability Scanning** - Trivy integration in CI/CD pipeline
- ✅ **Code Quality** - SonarQube with security hotspot detection  
- ✅ **Container Security** - Distroless images, non-root users
- ✅ **Authentication** - Keycloak SSO with OAuth2/OIDC protocols
- ✅ **Network Security** - Service mesh communication encryption

### **Quality Gates**
- **Test Coverage** - Minimum 80% code coverage requirement
- **Security Scanning** - Zero critical vulnerabilities policy
- **Code Quality** - SonarQube quality gates enforcement
- **Performance** - Load testing and performance benchmarks

---

## � **Quick Start Guide**

### **Prerequisites**
- Java 21, Maven 3.9+, Docker, Kubernetes
- GitLab account, Azure subscription (for production)

### **1. Local Development Setup**
```bash
# Clone repository
git clone https://gitlab.com/ouchajaaamine/helpdesk.git
cd helpdesk

# Start Kubernetes cluster
minikube start

# Deploy via GitLab CI/CD
# Push to 'k8s-local' branch → Triggers automated deployment

# Access applications
kubectl get services
```

### **2. Production Deployment**
```bash
# Azure infrastructure provisioning
cd terraform
terraform init && terraform apply

# Production deployment via CI/CD
# Push to 'main' branch → Triggers Azure VM deployment
```

---

## 📈 **Monitoring & Observability**

### **Metrics & Dashboards**
- **Application Metrics** - JVM, HTTP requests, database connections
- **Business Metrics** - User registrations, ticket processing rates  
- **Infrastructure Metrics** - CPU, memory, disk, network utilization
- **Custom Dashboards** - Real-time monitoring with Grafana

### **Health Monitoring**
- **Service Discovery** - Automatic service health detection
- **Circuit Breakers** - Fault tolerance and resilience patterns
- **Distributed Tracing** - Request flow across microservices
- **Alerting** - Prometheus alerts with Grafana notifications

---

## 🏗️ **Project Structure**

```
helpdesk/
├── 📁 backend/                    # Microservices source code
│   ├── discovery-service/         # Eureka service registry
│   ├── gateway-service/           # API Gateway with routing
│   ├── user-service/             # User management & auth
│   └── ticket-service/           # Business logic core
├── 📁 kubernetes/                # K8s deployment manifests  
│   ├── *-deployment.yaml        # Service deployments
│   ├── prometheus-*.yaml        # Monitoring configuration
│   └── grafana-*.yaml           # Dashboard setup
├── 📁 terraform/                # Infrastructure as Code
│   ├── main.tf                  # Azure resources
│   ├── variables.tf             # Configuration variables
│   └── outputs.tf               # Resource outputs
├── 📁 keycloak-import/          # SSO configuration
├── 📄 .gitlab-ci.yml            # Complete CI/CD pipeline
├── 📄 docker-compose.yml        # Local development
└── 📄 gitlab-pipeline.eraser    # Architecture diagram
```

---

## 🎨 **Architecture Highlights**

### **Design Patterns Implemented**
- **Microservices Architecture** - Domain-driven service boundaries
- **API Gateway Pattern** - Centralized routing and cross-cutting concerns
- **Service Discovery** - Dynamic service registration and health checks
- **Circuit Breaker** - Fault tolerance and resilience implementation
- **CQRS** - Command Query Responsibility Segregation in services

### **DevOps Best Practices**
- **Infrastructure as Code** - Terraform for reproducible deployments
- **GitOps Workflow** - Git-based deployment automation
- **Container Security** - Multi-stage builds, vulnerability scanning
- **Observability** - Comprehensive monitoring and alerting
- **Automated Testing** - Unit, integration, and security testing

---

## 🏆 **Professional Achievements**

### **Enterprise-Grade Implementation**
✅ **Production-Ready** - Complete enterprise architecture with monitoring  
✅ **Security-First** - Comprehensive security scanning and authentication  
✅ **Cloud-Native** - Kubernetes orchestration with Azure cloud integration  
✅ **DevSecOps** - Automated pipeline with security gates and quality checks  
✅ **Scalable Design** - Microservices with load balancing and auto-scaling  

### **Technical Excellence**
- **Zero-Downtime Deployments** - Blue-green deployment strategies
- **Infrastructure Automation** - Complete Terraform Azure provisioning  
- **Monitoring Excellence** - Prometheus + Grafana observability stack
- **Security Integration** - Trivy + SonarQube + Keycloak authentication
- **Quality Assurance** - Comprehensive testing with coverage reporting

---

## 🤝 **Professional Contact**

**LinkedIn**: [Your LinkedIn Profile]  
**GitLab**: [https://gitlab.com/ouchajaaamine/helpdesk](https://gitlab.com/ouchajaaamine/helpdesk)  
**Portfolio**: [Your Portfolio Website]

---

## 📋 **Technologies Showcase**

| Category | Technologies |
|----------|-------------|
| **Backend** | Spring Boot 3.x, Java 21, Maven, Spring Cloud |
| **Security** | Keycloak, OAuth2/OIDC, Spring Security, RBAC |
| **Databases** | PostgreSQL, Connection Pooling, Migration Scripts |
| **Containers** | Docker, Multi-stage builds, Distroless images |
| **Orchestration** | Kubernetes, Minikube, Azure AKS |
| **Cloud** | Microsoft Azure, Virtual Machines, Networking |
| **Infrastructure** | Terraform, Infrastructure as Code, State Management |
| **CI/CD** | GitLab CI/CD, Pipeline automation, Security gates |
| **Monitoring** | Prometheus, Grafana, Spring Actuator, Metrics |
| **Security** | Trivy, SonarQube, Vulnerability scanning |
| **Quality** | JUnit 5, Test coverage, Code quality gates |

---

*This project demonstrates enterprise-level software engineering practices suitable for production environments and showcases modern DevSecOps implementation with complete infrastructure automation.*
