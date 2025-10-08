#!/bin/bash
# =====================================================
# KEYCLOAK REALM SETUP FOR PRODUCTION DEPLOYMENT
# =====================================================
# This script ensures Keycloak realm data is properly
# transferred and imported on the VM

set -e

echo "🔑 Setting up Keycloak realm data for production deployment..."

# Create import directory structure on VM
echo "📁 Creating Keycloak import directory structure..."
mkdir -p /home/azureuser/helpdesk/keycloak-import

# Check if realm file exists in project
if [ -f "./keycloak-import/helpdesk-realm-complete.json" ]; then
    echo "✅ Found realm file: helpdesk-realm-complete.json"
    
    # Copy realm file to production location
    cp ./keycloak-import/helpdesk-realm-complete.json /home/azureuser/helpdesk/keycloak-import/
    echo "📋 Realm file copied to production directory"
    
    # Set proper permissions
    chmod 644 /home/azureuser/helpdesk/keycloak-import/helpdesk-realm-complete.json
    echo "🔒 File permissions set correctly"
    
else
    echo "⚠️  Realm file not found. Keycloak will start with default configuration."
    echo "🔧 You can import realm manually via Keycloak Admin Console at http://4.233.111.97:8180"
    
    # Create placeholder to ensure volume mount works
    echo '{}' > /home/azureuser/helpdesk/keycloak-import/placeholder.json
fi

echo "✅ Keycloak realm setup completed!"
echo ""
echo "📋 Keycloak Access Information:"
echo "   🌐 Admin Console: http://4.233.111.97:8180"
echo "   👤 Admin Username: admin"
echo "   🔑 Admin Password: admin123"
echo ""
echo "💾 Data Persistence:"
echo "   ✅ Keycloak data: Stored in Docker volume 'keycloak-data'"
echo "   ✅ Realm import: Stored in Docker volume 'keycloak-import'"
echo "   ✅ User database: Stored in Docker volume 'user_db_data'"
echo "   ✅ Ticket database: Stored in Docker volume 'ticket_db_data'"