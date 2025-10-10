# Local GitLab Runner Setup for K8s Deployment

## Why You Need a Local Runner

GitLab's shared runners (SaaS) **cannot** access your local Minikube cluster because:
- Your Minikube is at `127.0.0.1:49750` (localhost)
- GitLab runners are in the cloud
- They cannot reach your local network

## Solution: Install GitLab Runner Locally

### Step 1: Install GitLab Runner on Windows

```powershell
# Download GitLab Runner
Invoke-WebRequest -Uri "https://gitlab-runner-downloads.s3.amazonaws.com/latest/binaries/gitlab-runner-windows-amd64.exe" -OutFile "C:\GitLab-Runner\gitlab-runner.exe"

# Or use Chocolatey
choco install gitlab-runner
```

### Step 2: Register the Runner

```powershell
# Navigate to GitLab Runner directory
cd C:\GitLab-Runner

# Register the runner
.\gitlab-runner.exe register
```

When prompted, enter:
- **GitLab URL**: `https://gitlab.com`
- **Registration token**: Get from https://gitlab.com/ouchajaaamine/helpdesk/-/settings/ci_cd → Runners
- **Description**: `local-k8s-runner`
- **Tags**: `k8s-local,windows`
- **Executor**: `shell`

### Step 3: Install and Start the Service

```powershell
# Install as Windows service
.\gitlab-runner.exe install

# Start the service
.\gitlab-runner.exe start
```

### Step 4: Update .gitlab-ci.yml to Use Local Runner

Update the deploy job to use your local runner:

```yaml
deploy-to-k8s-local:
  stage: deploy
  tags:
    - k8s-local  # Use your local runner
  script:
    - kubectl apply -f kubernetes/
  only:
    - k8s-local
  when: manual
```

### Step 5: Remove KUBE_CONFIG Variable

When using a local runner, it will use your local kubectl configuration automatically - no need to set `KUBE_CONFIG` variable!

---

## Alternative: Simplified Approach (FASTER)

Since you want a fast deployment without building everything, here's the simplest solution:

### Use a Simple Deployment Script Locally

Instead of GitLab CI, create a local deployment script:

**File: `deploy-k8s-local.ps1`**

```powershell
# Simple local K8s deployment script
Write-Host "🚀 Deploying to local Kubernetes..." -ForegroundColor Green

# Apply all Kubernetes configs
kubectl apply -f kubernetes/postgres-user-db.yaml
kubectl apply -f kubernetes/postgres-ticket-db.yaml
kubectl apply -f kubernetes/discovery-service.yaml
kubectl apply -f kubernetes/keycloak.yaml

Write-Host "⏳ Waiting for infrastructure..." -ForegroundColor Yellow
Start-Sleep -Seconds 20

kubectl apply -f kubernetes/gateway-service.yaml
kubectl apply -f kubernetes/user-service.yaml
kubectl apply -f kubernetes/ticket-service.yaml

Write-Host "⏳ Waiting for services to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

Write-Host "✅ Deployment complete! Checking status..." -ForegroundColor Green
kubectl get pods
kubectl get services

$minikubeIp = minikube ip
Write-Host "`n📊 Access your services at:" -ForegroundColor Cyan
Write-Host "Eureka: http://$minikubeIp:30761" -ForegroundColor White
Write-Host "Gateway: http://$minikubeIp:30080" -ForegroundColor White
Write-Host "Keycloak: http://$minikubeIp:30180" -ForegroundColor White
```

**Usage:**
```powershell
.\deploy-k8s-local.ps1
```

This is **INSTANT** - no waiting for GitLab pipelines!

---

## Quick Decision Guide

| Approach | Speed | Complexity | Best For |
|----------|-------|------------|----------|
| **Local Script** | ⚡ Instant | 🟢 Simple | Learning K8s locally |
| **Local GitLab Runner** | 🚀 Fast | 🟡 Medium | CI/CD practice |
| **Remote K8s Cluster** | ⏱️ Depends | 🔴 Complex | Real production |

### My Recommendation for Learning:

**Use the local PowerShell script** (`deploy-k8s-local.ps1`) because:
1. ✅ Instant deployment
2. ✅ No GitLab Runner setup needed
3. ✅ Perfect for learning
4. ✅ You can still learn CI/CD concepts
5. ✅ Later, set up local runner when you want to practice full CI/CD

Would you like me to:
1. Create the simple local deployment script? (FASTEST)
2. Help you set up a local GitLab Runner? (FULL CI/CD)
3. Remove the K8s deployment from GitLab CI entirely? (CLEAN SEPARATION)
