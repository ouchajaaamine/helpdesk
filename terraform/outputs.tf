# =====================================================
# HELPDESK PROJECT - OUTPUTS
# =====================================================
# Shows useful information after Terraform creates resources
# =====================================================

# Show the resource group name that was created
output "resource_group_name" {
  description = "The name of the created resource group"
  value       = azurerm_resource_group.helpdesk.name
}

# Show the Azure region where everything was created
output "location" {
  description = "The Azure region where resources were created"
  value       = azurerm_resource_group.helpdesk.location
}

# Show the resource group ID (useful for other tools)
output "resource_group_id" {
  description = "The ID of the created resource group"
  value       = azurerm_resource_group.helpdesk.id
}

# Show the public IP address of the VM
output "vm_public_ip" {
  description = "The public IP address of the virtual machine"
  value       = azurerm_public_ip.helpdesk.ip_address
}

# Show the SSH connection command
output "ssh_connection" {
  description = "SSH command to connect to the VM"
  value       = "ssh azureuser@${azurerm_public_ip.helpdesk.ip_address}"
}