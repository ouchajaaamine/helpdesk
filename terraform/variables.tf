# =====================================================
# HELPDESK PROJECT - ESSENTIAL VARIABLES ONLY
# =====================================================
# Just the basics you need to get started
# =====================================================

# Environment - always "dev" for this project
variable "environment" {
  description = "The deployment environment"
  type        = string
  default     = "dev"
}

# Azure region where everything gets created
variable "location" {
  description = "The Azure region where resources will be created"
  type        = string
  default     = "France Central"
}