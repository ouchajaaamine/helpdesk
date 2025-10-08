# =====================================================
# HELPDESK PROJECT - AZURE INFRASTRUCTURE
# =====================================================
# This Terraform configuration creates the complete Azure infrastructure
# to deploy the helpdesk microservices using Docker Compose
#
# Architecture:
# - Resource Group (container for all resources)
# - Virtual Network + Subnet (networking foundation)
# - Network Security Group (firewall rules)
# - Virtual Machine (Ubuntu server for Docker Compose)
# - Network Interface (VM network connection)
# =====================================================

# Configure Terraform and required providers
terraform {
  required_version = ">= 1.0"
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.80"
    }
  }
}

# Configure the Azure Provider
provider "azurerm" {
  features {}
}

# =====================================================
# RESOURCE GROUP
# =====================================================
# Container for all Azure resources in this project
# Provides organization, cost tracking, and bulk operations

# Resource Group - Container for all resources
resource "azurerm_resource_group" "helpdesk" {
  name     = "${var.environment}-helpdesk-rg"
  location = var.location

  tags = {
    Environment = var.environment
    Project     = "helpdesk"
    Owner       = "student"
    CreatedBy   = "terraform"
  }
}

# =====================================================
# NETWORKING
# =====================================================
# Virtual Network - Private network in Azure
# Subnet - Section of the network for VM
# Network Security Group - Firewall rules

# Virtual Network - Like your private network in Azure
resource "azurerm_virtual_network" "helpdesk" {
  name                = "${var.environment}-helpdesk-vnet"
  address_space       = ["10.0.0.0/16"]
  location            = azurerm_resource_group.helpdesk.location
  resource_group_name = azurerm_resource_group.helpdesk.name

  tags = {
    Environment = var.environment
    Project     = "helpdesk"
    Owner       = "student"
    CreatedBy   = "terraform"
  }
}

# Subnet - Section of the network where VM will live
resource "azurerm_subnet" "helpdesk" {
  name                 = "${var.environment}-helpdesk-subnet"
  resource_group_name  = azurerm_resource_group.helpdesk.name
  virtual_network_name = azurerm_virtual_network.helpdesk.name
  address_prefixes     = ["10.0.1.0/24"]
}

# Network Security Group - Firewall rules for your services
resource "azurerm_network_security_group" "helpdesk" {
  name                = "${var.environment}-helpdesk-nsg"
  location            = azurerm_resource_group.helpdesk.location
  resource_group_name = azurerm_resource_group.helpdesk.name

  # Allow SSH access (port 22)
  security_rule {
    name                       = "SSH"
    priority                   = 1001
    direction                  = "Inbound"
    access                     = "Allow"
    protocol                   = "Tcp"
    source_port_range          = "*"
    destination_port_range     = "22"
    source_address_prefix      = "*"
    destination_address_prefix = "*"
  }

  # Allow HTTP access (port 80) for your web services
  security_rule {
    name                       = "HTTP"
    priority                   = 1002
    direction                  = "Inbound"
    access                     = "Allow"
    protocol                   = "Tcp"
    source_port_range          = "*"
    destination_port_range     = "80"
    destination_address_prefix = "*"
    source_address_prefix      = "*"
  }

  # Allow HTTPS access (port 443)
  security_rule {
    name                       = "HTTPS"
    priority                   = 1003
    direction                  = "Inbound"
    access                     = "Allow"
    protocol                   = "Tcp"
    source_port_range          = "*"
    destination_port_range     = "443"
    source_address_prefix      = "*"
    destination_address_prefix = "*"
  }

  # Allow custom ports for your microservices (8080-8090)
  security_rule {
    name                       = "Microservices"
    priority                   = 1004
    direction                  = "Inbound"
    access                     = "Allow"
    protocol                   = "Tcp"
    source_port_range          = "*"
    destination_port_range     = "8080-8090"
    source_address_prefix      = "*"
    destination_address_prefix = "*"
  }

  tags = {
    Environment = var.environment
    Project     = "helpdesk"
    Owner       = "student"
    CreatedBy   = "terraform"
  }
}

# =====================================================
# PUBLIC IP AND NETWORK INTERFACE
# =====================================================
# Public IP - So you can access VM from internet
# Network Interface - Connects VM to the network

# Public IP for VM access
resource "azurerm_public_ip" "helpdesk" {
  name                = "${var.environment}-helpdesk-pip"
  location            = azurerm_resource_group.helpdesk.location
  resource_group_name = azurerm_resource_group.helpdesk.name
  allocation_method   = "Static"
  sku                = "Standard"

  tags = {
    Environment = var.environment
    Project     = "helpdesk"
    Owner       = "student"
    CreatedBy   = "terraform"
  }
}

# Network Interface - Connects VM to subnet and public IP
resource "azurerm_network_interface" "helpdesk" {
  name                = "${var.environment}-helpdesk-nic"
  location            = azurerm_resource_group.helpdesk.location
  resource_group_name = azurerm_resource_group.helpdesk.name

  ip_configuration {
    name                          = "internal"
    subnet_id                     = azurerm_subnet.helpdesk.id
    private_ip_address_allocation = "Dynamic"
    public_ip_address_id          = azurerm_public_ip.helpdesk.id
  }

  tags = {
    Environment = var.environment
    Project     = "helpdesk"
    Owner       = "student"
    CreatedBy   = "terraform"
  }
}

# Connect Network Security Group to Network Interface
resource "azurerm_network_interface_security_group_association" "helpdesk" {
  network_interface_id      = azurerm_network_interface.helpdesk.id
  network_security_group_id = azurerm_network_security_group.helpdesk.id
}

# =====================================================
# VIRTUAL MACHINE
# =====================================================
# Ubuntu VM where Docker Compose will run your services

# Virtual Machine - Ubuntu server for Docker
resource "azurerm_linux_virtual_machine" "helpdesk" {
  name                = "${var.environment}-helpdesk-vm"
  location            = azurerm_resource_group.helpdesk.location
  resource_group_name = azurerm_resource_group.helpdesk.name
  size                = "Standard_B2s"  # 2 vCPUs, 4GB RAM - good for students
  admin_username      = "azureuser"

  # Disable password authentication and use SSH keys
  disable_password_authentication = true

  network_interface_ids = [
    azurerm_network_interface.helpdesk.id,
  ]

  admin_ssh_key {
    username   = "azureuser"
    public_key = file("~/.ssh/id_rsa.pub")  # Your SSH public key
  }

  os_disk {
    caching              = "ReadWrite"
    storage_account_type = "Premium_LRS"
  }

  source_image_reference {
    publisher = "Canonical"
    offer     = "0001-com-ubuntu-server-jammy"
    sku       = "22_04-lts-gen2"
    version   = "latest"
  }

  # Custom script to install Docker and Docker Compose
  custom_data = base64encode(<<-EOF
              #!/bin/bash
              
              # Update system
              apt-get update
              apt-get upgrade -y
              
              # Install Docker
              apt-get install -y ca-certificates curl gnupg lsb-release
              mkdir -p /etc/apt/keyrings
              curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
              echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
              apt-get update
              apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
              
              # Add user to docker group
              usermod -aG docker azureuser
              
              # Enable Docker service
              systemctl enable docker
              systemctl start docker
              
              # Install Docker Compose standalone
              curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
              chmod +x /usr/local/bin/docker-compose
              
              # Install Git
              apt-get install -y git
              
              # Create directory for the project
              mkdir -p /home/azureuser/helpdesk
              chown azureuser:azureuser /home/azureuser/helpdesk
              
              EOF
  )

  tags = {
    Environment = var.environment
    Project     = "helpdesk"
    Owner       = "student"
    CreatedBy   = "terraform"
  }
}