# Kubernetes CI/CD Pipeline Setup Guide

## 📋 Overview

This guide explains how to set up the GitLab CI/CD pipeline for automatic deployment to your local Kubernetes cluster (Minikube) using the `k8s-local` branch.

## 🎯 Pipeline Flow for k8s-local Branch

```
k8s-local branch push
  ↓
Build Stage (Maven builds)
  ↓
Test Stage (Unit tests + SonarQube)
  ↓
Docker Build (Build images locally)
  ↓
Security Scan (Trivy scans)
  ↓
Docker Push (Push to GitLab Registry)
  ↓
Deploy to K8s (kubectl apply) ← MANUAL TRIGGER
  ↓
Services running in Kubernetes
```

## 🔧 Prerequisites

### 1. Local Kubernetes Cluster
Ensure you have a running Kubernetes cluster (Minikube, Kind, or Docker Desktop):

```powershell
# Check if Minikube is running
minikube status

# If not running, start it
minikube start
```

### 2. GitLab CI/CD Variables

You need to configure the following CI/CD variables in your GitLab project:

#### Go to: **Settings → CI/CD → Variables**

Add these variables:

| Variable Name | Value | Protected | Masked | Description |
|--------------|-------|-----------|--------|-------------|
| `KUBE_CONFIG` | *Your kubeconfig file (base64 encoded)* | ✅ | ❌ | Kubernetes cluster connection config |
| `GITLAB_USER_EMAIL` | your-email@example.com | ❌ | ❌ | Your GitLab email for Docker registry |
| `CI_REGISTRY_USER` | *Already exists* | - | - | GitLab Registry username |
| `CI_REGISTRY_PASSWORD` | *Already exists* | - | - | GitLab Registry password |

### 3. Get Your KUBE_CONFIG

#### Option A: From Minikube (Recommended for Local)

```powershell
# Get your kubeconfig and encode it to base64
kubectl config view --raw --minify | Out-File -Encoding UTF8 kubeconfig.yaml
[Convert]::ToBase64String([IO.File]::ReadAllBytes("kubeconfig.yaml")) | Set-Content kubeconfig-base64.txt

# Copy the content of kubeconfig-base64.txt
Get-Content kubeconfig-base64.txt | Set-Clipboard
```

Then paste this base64 string as the `KUBE_CONFIG` variable in GitLab.

#### Option B: Using Minikube Tunnel (For GitLab Runner on Same Machine)

If your GitLab Runner runs on the same machine as Minikube:

```powershell
# Start Minikube tunnel to expose cluster
minikube tunnel

# In another terminal, get the kubeconfig
kubectl config view --raw --minify
```

### 4. GitLab Registry Secret

The pipeline automatically creates the `gitlab-registry-secret` in your cluster. No manual action needed!

## 🚀 How to Use the Pipeline

### Step 1: Commit Your Changes to k8s-local Branch

```powershell
# Make sure you're on k8s-local branch
git checkout k8s-local

# Add your changes
git add .
git commit -m "Update Kubernetes configurations"
git push origin k8s-local
```

### Step 2: Monitor the Pipeline

1. Go to **CI/CD → Pipelines** in GitLab
2. You'll see the pipeline running through these stages:
   - ✅ Build (all services)
   - ✅ Test (unit tests + SonarQube)
   - ✅ Docker Build (local images)
   - ✅ Security Scan (Trivy)
   - ✅ Docker Push (to GitLab Registry)
   - ⏸️ Deploy to K8s (WAITING - manual trigger)

### Step 3: Manually Trigger Deployment

1. When all previous stages pass, you'll see the **deploy-to-k8s-local** job
2. Click the **▶️ Play** button to trigger deployment
3. Watch the deployment logs

### Step 4: Verify Deployment

After deployment completes, check your services:

```powershell
# Get Minikube IP
minikube ip
# Example output: 192.168.49.2

# Check all pods are running
kubectl get pods

# Check all services
kubectl get services
```

Access your services:
- **Eureka Dashboard**: http://192.168.49.2:30761
- **API Gateway**: http://192.168.49.2:30080
- **Keycloak Admin**: http://192.168.49.2:30180
- **User Service**: http://192.168.49.2:30081
- **Ticket Service**: http://192.168.49.2:30082

## 🔍 Troubleshooting

### Issue 1: Pipeline Can't Connect to Cluster

**Error**: `The connection to the server localhost:8080 was refused`

**Solution**:
```powershell
# Regenerate your KUBE_CONFIG
kubectl config view --raw --minify | Out-File -Encoding UTF8 kubeconfig.yaml
[Convert]::ToBase64String([IO.File]::ReadAllBytes("kubeconfig.yaml"))

# Update the KUBE_CONFIG variable in GitLab with the new value
```

### Issue 2: ImagePullBackOff

**Error**: Pods show `ImagePullBackOff` status

**Solution**:
```powershell
# Check if gitlab-registry-secret exists
kubectl get secrets

# If missing, manually create it
kubectl create secret docker-registry gitlab-registry-secret \
  --docker-server=registry.gitlab.com \
  --docker-username=YOUR_GITLAB_USERNAME \
  --docker-password=YOUR_GITLAB_TOKEN \
  --docker-email=YOUR_EMAIL
```

### Issue 3: Services Not Ready

**Error**: Services stuck in `Init:0/2` or `PodInitializing`

**Solution**:
```powershell
# Check init container logs
kubectl logs POD_NAME -c wait-for-postgres
kubectl logs POD_NAME -c wait-for-discovery

# Ensure PostgreSQL and discovery-service are running first
kubectl get pods -l app=postgres-user-db
kubectl get pods -l app=discovery-service
```

### Issue 4: PostgreSQL Restarting

**Error**: PostgreSQL pods keep restarting

**Solution**: Health checks are already disabled in your YAML files. If still restarting:
```powershell
# Check logs
kubectl logs postgres-user-db-xxxxx

# Check persistent volume
kubectl get pvc
```

## 📊 Pipeline Job Details

### deploy-to-k8s-local

**What it does**:
1. ✅ Creates GitLab Registry authentication secret
2. ✅ Deploys PostgreSQL databases
3. ✅ Deploys Eureka Discovery Service
4. ✅ Deploys Keycloak
5. ✅ Deploys Gateway, User, and Ticket services
6. ✅ Waits for services to be ready
7. ✅ Shows deployment summary

**Deployment Order** (Important!):
```
1. PostgreSQL databases (infrastructure)
2. Discovery Service (service registry)
3. Keycloak (authentication)
4. Application Services (gateway, user, ticket)
```

## 🎓 Learning Notes

### Why Manual Trigger?

The `when: manual` setting prevents accidental deployments to your cluster. You have full control over when to deploy.

### Why Separate from Main Branch?

- `main` branch → Azure VM deployment (production)
- `k8s-local` branch → Local Kubernetes (learning/testing)

This keeps your production environment safe while you experiment with Kubernetes!

### Health Checks

Currently disabled to keep things simple for learning:
- Readiness probes: Commented out
- Liveness probes: Commented out

You can enable them later once you're comfortable with the basics.

## 🔄 Making Changes

### To Update Kubernetes Configurations:

```powershell
# 1. Make changes to YAML files
code kubernetes/user-service.yaml

# 2. Commit and push
git add kubernetes/
git commit -m "Update user-service configuration"
git push origin k8s-local

# 3. Wait for pipeline to build and push images
# 4. Manually trigger deploy-to-k8s-local job
# 5. Verify deployment
kubectl get pods
```

### To Update Application Code:

```powershell
# 1. Make changes to Java code
code backend/user-service/src/...

# 2. Commit and push
git add backend/
git commit -m "Fix user service bug"
git push origin k8s-local

# 3. Pipeline will:
#    - Build new JAR
#    - Run tests
#    - Build new Docker image
#    - Scan for vulnerabilities
#    - Push to GitLab Registry
# 4. Manually trigger deployment
# 5. New pods will pull the latest image
```

## ✅ Success Criteria

Your deployment is successful when you see:

```powershell
kubectl get pods

NAME                                  READY   STATUS    RESTARTS   AGE
discovery-service-xxxxx               1/1     Running   0          5m
gateway-service-xxxxx                 1/1     Running   0          4m
keycloak-xxxxx                        1/1     Running   0          4m
postgres-ticket-db-xxxxx              1/1     Running   0          5m
postgres-user-db-xxxxx                1/1     Running   0          5m
ticket-service-xxxxx                  1/1     Running   0          3m
user-service-xxxxx                    1/1     Running   0          3m
```

All services showing **1/1 Running** = Perfect! 🎉

## 🎯 Next Steps

1. ✅ Set up CI/CD variables in GitLab
2. ✅ Push to k8s-local branch
3. ✅ Watch pipeline execute
4. ✅ Manually trigger deployment
5. ✅ Access your services
6. 🎓 Learn and experiment!

---

**Alhamdulillah!** Your Kubernetes CI/CD pipeline is ready! 🚀
