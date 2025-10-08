# Script to properly encode SSH private key for GitLab CI/CD variable

Write-Host "=== SSH Key Encoder for GitLab ===" -ForegroundColor Cyan

$sshKeyPath = "C:\Users\pc\.ssh\id_rsa"

if (Test-Path $sshKeyPath) {
    Write-Host "Found SSH key" -ForegroundColor Green
    $keyContent = Get-Content $sshKeyPath -Raw
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($keyContent)
    $base64 = [Convert]::ToBase64String($bytes)
    
    Write-Host ""
    Write-Host "Base64 Encoded Key:" -ForegroundColor Yellow
    Write-Host $base64
    Write-Host ""
    
    Set-Clipboard -Value $base64
    Write-Host "Copied to clipboard! Paste this into GitLab AZURE_VM_SSH_KEY variable" -ForegroundColor Green
} else {
    Write-Host "SSH key not found at: $sshKeyPath" -ForegroundColor Red
    Write-Host "Please create an SSH key first or provide the correct path" -ForegroundColor Yellow
}
