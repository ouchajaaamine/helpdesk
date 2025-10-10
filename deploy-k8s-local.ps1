# 🚀 Fast Local Kubernetes Deployment Script
# Deploy all services to your local Minikube cluster instantly!

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   Local Kubernetes Deployment" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Check if Minikube is running
Write-Host "🔍 Checking Minikube status..." -ForegroundColor Yellow
$minikubeStatus = minikube status 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Minikube is not running!" -ForegroundColor Red
    Write-Host "Please start Minikube with: minikube start" -ForegroundColor Yellow
    exit 1
}
Write-Host "✅ Minikube is running" -ForegroundColor Green

# Deploy infrastructure services
Write-Host "`n📦 Deploying infrastructure services..." -ForegroundColor Yellow
kubectl apply -f kubernetes/postgres-user-db.yaml
kubectl apply -f kubernetes/postgres-ticket-db.yaml
kubectl apply -f kubernetes/discovery-service.yaml

Write-Host "⏳ Waiting 20 seconds for infrastructure to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 20

# Deploy Keycloak
Write-Host "`n🔐 Deploying Keycloak..." -ForegroundColor Yellow
kubectl apply -f kubernetes/keycloak.yaml

# Deploy application services
Write-Host "`n🚀 Deploying application services..." -ForegroundColor Yellow
kubectl apply -f kubernetes/gateway-service.yaml
kubectl apply -f kubernetes/user-service.yaml
kubectl apply -f kubernetes/ticket-service.yaml

Write-Host "`n⏳ Waiting 30 seconds for services to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Display deployment status
Write-Host "`n========================================" -ForegroundColor Green
Write-Host "   Deployment Status" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Green

kubectl get pods
Write-Host ""
kubectl get services

# Get Minikube IP
$minikubeIp = (minikube ip).Trim()

# Display service URLs
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   🌐 Service Access URLs" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Eureka Dashboard:  http://${minikubeIp}:30761" -ForegroundColor White
Write-Host "API Gateway:       http://${minikubeIp}:30080" -ForegroundColor White
Write-Host "Keycloak Admin:    http://${minikubeIp}:30180" -ForegroundColor White
Write-Host "User Service:      http://${minikubeIp}:30081" -ForegroundColor White
Write-Host "Ticket Service:    http://${minikubeIp}:30082" -ForegroundColor White
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "✅ Deployment complete!" -ForegroundColor Green
Write-Host "💡 Tip: Run 'kubectl get pods' to check pod status anytime`n" -ForegroundColor Yellow
