# Monitoring CI/CD Integration Guide

## Overview
This document explains how Prometheus and Grafana monitoring is integrated into the GitLab CI/CD pipeline for automated deployment to Kubernetes.

## Pipeline Integration

### Monitoring Deployment Job: `deploy-monitoring-k8s`

The monitoring stack deployment is now fully integrated into the GitLab CI/CD pipeline with the following characteristics:

#### **Stage**: `deploy`
- Runs after the main application services are deployed
- Part of the same deployment stage as other Kubernetes deployments

#### **Dependencies**: 
- Requires all Docker images to be built and pushed first
- Depends on `deploy-to-k8s-local` to ensure services are running before monitoring

#### **Triggers**:
- **Branch**: `k8s-local` or `monitoring`
- **Execution**: Manual trigger (for safety)
- **Failure**: Allowed to fail (won't break pipeline)

## Deployment Process

### 1. Pre-checks
- Verifies Kubernetes cluster connectivity
- Checks kubectl configuration

### 2. Service Readiness
- Waits for main services to be ready:
  - discovery-service
  - gateway-service  
  - user-service
  - ticket-service

### 3. Prometheus Deployment
```bash
kubectl apply -f kubernetes/prometheus-rbac.yaml
kubectl apply -f kubernetes/prometheus-configmap.yaml  
kubectl apply -f kubernetes/prometheus-deployment.yaml
```

### 4. Grafana Deployment
```bash
kubectl apply -f kubernetes/grafana-configmap.yaml
kubectl apply -f kubernetes/grafana-deployment.yaml
```

### 5. Verification
- Checks pod readiness with timeout (180s)
- Displays monitoring stack status
- Provides access URLs
- Verifies Prometheus targets health

## Access Information

After successful deployment, the monitoring stack is accessible at:

- **Prometheus**: `http://localhost:30090`
- **Grafana**: `http://localhost:30300`
  - Username: `admin`
  - Password: `admin123`

## Pipeline Usage

### Automatic Workflow
1. Push code to `k8s-local` or `monitoring` branch
2. Pipeline runs through all stages automatically
3. Manual trigger required for deployment jobs
4. Deploy main services first: `deploy-to-k8s-local`
5. Deploy monitoring stack: `deploy-monitoring-k8s`

### Manual Commands
If cluster is not accessible from GitLab runner:
```bash
# Deploy monitoring stack manually
kubectl apply -f kubernetes/prometheus-rbac.yaml
kubectl apply -f kubernetes/prometheus-configmap.yaml  
kubectl apply -f kubernetes/prometheus-deployment.yaml
kubectl apply -f kubernetes/grafana-configmap.yaml
kubectl apply -f kubernetes/grafana-deployment.yaml
```

## Monitoring Targets

The Prometheus configuration automatically discovers and monitors:

1. **prometheus** (self-monitoring)
2. **discovery-service** - Service registry metrics
3. **gateway-service** - API gateway metrics  
4. **user-service** - User management metrics
5. **ticket-service** - Ticket system metrics

All services expose metrics at `/actuator/prometheus` endpoint on port 8080.

## Pipeline Environment

- **Environment Name**: `k8s-monitoring`
- **Environment URL**: `http://localhost:30300` (Grafana)
- **Kubernetes Context**: Local Minikube cluster

## Troubleshooting

### Common Issues:

1. **Cluster Not Accessible**
   - Pipeline will show friendly message
   - Use manual kubectl commands instead

2. **Services Not Ready**
   - Pipeline waits up to 120s for main services
   - Check pod status: `kubectl get pods`

3. **Monitoring Pods Failing**
   - Check logs: `kubectl logs -l app=prometheus`
   - Verify RBAC permissions are applied

4. **Targets Not UP**
   - Ensure services have Prometheus metrics enabled
   - Check service discovery configuration
   - Verify network connectivity between pods

## Security Notes

- Monitoring deployment uses same GitLab Registry credentials
- Prometheus RBAC provides minimal required permissions
- Grafana uses basic authentication (change default password)
- Pipeline requires manual trigger for safety

## Next Steps

After deployment:
1. Access Grafana at `http://localhost:30300`
2. Prometheus data source is pre-configured
3. Create custom dashboards for your microservices
4. Set up alerting rules in Prometheus
5. Configure notification channels in Grafana
