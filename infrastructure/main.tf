# ------------------------------------------------------------------
#  PROVIDER CONFIGURATION
# ------------------------------------------------------------------

terraform {
  required_providers {
    azuredevops = {
      source  = "microsoft/azuredevops"
      version = ">= 0.4.0"
    }
    azurerm = {
      source  = "hashicorp/azurerm"
      # This line forces Terraform to use a newer version
      version = ">= 3.74.0"
    }
  }
}

provider "azuredevops" {
  org_service_url = "https://dev.azure.com/reccloudcomputingproject"
}

provider "azurerm" {
  features {}
}

# ------------------------------------------------------------------
#  AZURE INFRASTRUCTURE (The Home for Your Microservices)
# ------------------------------------------------------------------

resource "azurerm_resource_group" "rg" {
  name     = "mesh-project-rg"
  location = "Central India"
}

# ------------------------------------------------------------------
#  AZURE CONTAINER APPS (Cost-Effective Microservices Host)
# ------------------------------------------------------------------

resource "azurerm_container_app_environment" "aca_env" {
  name                       = "mesh-aca-env"
  location                   = azurerm_resource_group.rg.location
  resource_group_name        = azurerm_resource_group.rg.name
  # Consumption Plan is required for the free tier (scales to zero)
  infrastructure_resource_group_name = "${azurerm_resource_group.rg.name}-infra"
  log_analytics_workspace_id = null

  workload_profile {
    name = "Consumption"
    workload_profile_type = "Consumption"
  }
}

# 1. API Gateway (EXTERNAL FACING)
resource "azurerm_container_app" "gateway_app" {
  name                    = "api-gateway-app"
  container_app_environment_id = azurerm_container_app_environment.aca_env.id
  resource_group_name     = azurerm_resource_group.rg.name
  revision_mode           = "Single"
  template {
    container {
      name   = "api-gateway"
      image  = "yourregistry/api-gateway:latest" # Placeholder: Update with your ACR path
      cpu    = 0.5
      memory = "1.0Gi"
    }
    scale {
      min_replicas = 0 # KEY: Scales down to zero when idle
      max_replicas = 1
    }
  }

  ingress {
    external_enabled = true  # PUBLIC ACCESS
    target_port      = 8080
    transport        = "auto"
  traffic_weight {
    latest_revision = true
    percentage      = 100
  }
  }
}

# 2. User Service (INTERNAL ONLY)
resource "azurerm_container_app" "user_app" {
  name                    = "user-service-app"
  container_app_environment_id = azurerm_container_app_environment.aca_env.id
  resource_group_name     = azurerm_resource_group.rg.name
  revision_mode           = "Single"
  template {
    container {
      name   = "user-service"
      image  = "yourregistry/user-service:latest"
      cpu    = 0.5
      memory = "1.0Gi"
    }
    scale {
      min_replicas = 0
      max_replicas = 1
    }
  }

  ingress {
    external_enabled = false # INTERNAL ACCESS ONLY
    target_port      = 8080
    transport        = "auto"
    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }
}

# 3. Admin Service (INTERNAL ONLY)
resource "azurerm_container_app" "admin_app" {
  name                    = "admin-service-app"
  container_app_environment_id = azurerm_container_app_environment.aca_env.id
  resource_group_name     = azurerm_resource_group.rg.name
  revision_mode           = "Single"
  template {
    container {
      name   = "admin-service"
      image  = "yourregistry/admin-service:latest"
      cpu    = 0.5
      memory = "1.0Gi"
    }
    scale {
      min_replicas = 0
      max_replicas = 1
    }
  }

  ingress {
    external_enabled = false
    target_port      = 8080
    transport        = "auto"
    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }
}

# 4. Classroom Service (INTERNAL ONLY)
resource "azurerm_container_app" "classroom_app" {
  name                    = "classroom-service-app"
  container_app_environment_id = azurerm_container_app_environment.aca_env.id
  resource_group_name     = azurerm_resource_group.rg.name
  revision_mode           = "Single"
  template {
    container {
      name   = "classroom-service"
      image  = "yourregistry/classroom-service:latest"
      cpu    = 0.5
      memory = "1.0Gi"
    }
    scale {
      min_replicas = 0
      max_replicas = 1
    }
  }

  ingress {
    external_enabled = false
    target_port      = 8080
    transport        = "auto"
    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }
}

# 5. Discovery Server (INTERNAL ONLY)
resource "azurerm_container_app" "discovery_app" {
  name                    = "discovery-server-app"
  container_app_environment_id = azurerm_container_app_environment.aca_env.id
  resource_group_name     = azurerm_resource_group.rg.name
  revision_mode           = "Single"
  template {
    container {
      name   = "discovery-server"
      image  = "yourregistry/discovery-server:latest"
      cpu    = 0.5
      memory = "1.0Gi"
    }
    scale {
      min_replicas = 0
      max_replicas = 1
    }
  }

  ingress {
    external_enabled = false
    target_port      = 8080
    transport        = "auto"
    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }
}

# ------------------------------------------------------------------
#  AZURE DEVOPS PIPELINES (The Assembly Lines)
# ------------------------------------------------------------------

data "azuredevops_project" "project" {
  name = "Mesh"
}

# --- Pipeline for User Service ---
resource "azuredevops_build_definition" "user_pipeline" {
  project_id = data.azuredevops_project.project.id
  name       = "UserService-CI"

  repository {
    repo_type             = "GitHub"
    repo_id               = "GopinathR20/Mesh-Microservices"
    branch_name           = "development"
    service_connection_id = "GopinathR20"
    yml_path              = "Mesh-Microservices/user-service/azure-pipelines.yml"
  }
}
# --- Pipeline for Admin Service ---
resource "azuredevops_build_definition" "admin_pipeline" {
  project_id = data.azuredevops_project.project.id
  name       = "AdminService-CI"

  repository {
    repo_type             = "GitHub"
    repo_id               = "GopinathR20/Mesh-Microservices"
    branch_name           = "development"
    service_connection_id = "GopinathR20"
    yml_path              = "Mesh-Microservices/admin-service/azure-pipelines.yml"
  }
}
# --- Pipeline for Classroom Service ---
resource "azuredevops_build_definition" "classroom_pipeline" {
  project_id = data.azuredevops_project.project.id
  name       = "ClassroomService-CI"

  repository {
    repo_type             = "GitHub"
    repo_id               = "GopinathR20/Mesh-Microservices"
    branch_name           = "development"
    service_connection_id = "GopinathR20"
    yml_path              = "Mesh-Microservices/classroom-service/azure-pipelines.yml"
  }
}

# --- Pipeline for API Gateway ---
resource "azuredevops_build_definition" "gateway_pipeline" {
  project_id = data.azuredevops_project.project.id
  name       = "ApiGateway-CI"

  repository {
    repo_type             = "GitHub"
    repo_id               = "GopinathR20/Mesh-Microservices"
    branch_name           = "development"
    service_connection_id = "GopinathR20"
    yml_path              = "Mesh-Microservices/api-gateway/azure-pipelines.yml"
  }
}
# --- Pipeline for Discovery Server ---
resource "azuredevops_build_definition" "discovery_pipeline" {
  project_id = data.azuredevops_project.project.id
  name       = "DiscoveryServer-CI"

  repository {
    repo_type             = "GitHub"
    repo_id               = "GopinathR20/Mesh-Microservices"
    branch_name           = "development"
    service_connection_id = "GopinathR20"
    yml_path              = "Mesh-Microservices/discovery-server/azure-pipelines.yml"
  }
}