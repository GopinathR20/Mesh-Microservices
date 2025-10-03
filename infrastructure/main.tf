# ------------------------------------------------------------------
#  PROVIDER CONFIGURATION
# ------------------------------------------------------------------

terraform {
  required_providers {
    azuredevops = {
      source  = "microsoft/azuredevops"
      version = ">=0.4.0"
    }
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~>3.0"
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

resource "azurerm_spring_cloud_service" "asa" {
  name                = "mesh-spring-apps-env"
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku_name            = "B0"
}

# Create an app placeholder for each microservice
resource "azurerm_spring_cloud_app" "user_app" {
  name                = "user-service"
  resource_group_name = azurerm_resource_group.rg.name
  service_name        = azurerm_spring_cloud_service.asa.name
}

resource "azurerm_spring_cloud_app" "admin_app" {
  name                = "admin-service"
  resource_group_name = azurerm_resource_group.rg.name
  service_name        = azurerm_spring_cloud_service.asa.name
}

resource "azurerm_spring_cloud_app" "classroom_app" {
  name                = "classroom-service"
  resource_group_name = azurerm_resource_group.rg.name
  service_name        = azurerm_spring_cloud_service.asa.name
}

resource "azurerm_spring_cloud_app" "gateway_app" {
  name                = "api-gateway"
  resource_group_name = azurerm_resource_group.rg.name
  service_name        = azurerm_spring_cloud_service.asa.name
}

resource "azurerm_spring_cloud_app" "discovery_app" {
  name                = "discovery-server"
  resource_group_name = azurerm_resource_group.rg.name
  service_name        = azurerm_spring_cloud_service.asa.name
}


# ------------------------------------------------------------------
#  AZURE DEVOPS PIPELINES (The Assembly Lines)
# ------------------------------------------------------------------

data "azuredevops_project" "project" {
  name = "Mesh"
}

# --- Pipeline for User Service ---
# --- Pipeline for User Service ---
resource "azuredevops_build_definition" "user_pipeline" {
  project_id = data.azuredevops_project.project.id
  name       = "UserService-CI"

  repository {
    repo_type             = "GitHub"
    repo_id               = "GopinathR20/Mesh-Microservices"
    branch_name           = "development" # Or "main" if you prefer
    service_connection_id = "GopinathR20"
    # This points to the new, central YAML file
    yml_path              = "infrastructure/azure-pipelines.yml"
  }

  # This block passes the unique details for this specific service
  variables = {
    "serviceDirectory"  = "user-service"
    "serviceName"       = azurerm_spring_cloud_app.user_app.name
    "springAppName"     = azurerm_spring_cloud_service.asa.name
    "azureSubscription" = "Azure for Students"
  }
}
# --- Pipeline for Admin Service ---
# --- Pipeline for Admin Service ---
resource "azuredevops_build_definition" "admin_pipeline" { # <-- Change 1
  project_id = data.azuredevops_project.project.id
  name       = "AdminService-CI" # <-- Change 2

  repository {
    repo_type             = "GitHub"
    repo_id               = "GopinathR20/Mesh-Microservices"
    branch_name           = "development"
    service_connection_id = "GopinathR20"
    yml_path              = "infrastructure/azure-pipelines.yml"
  }

  variables = {
    "serviceDirectory"  = "admin-service" # <-- Change 3
    "serviceName"       = azurerm_spring_cloud_app.admin_app.name # <-- Change 4
    "springAppName"     = azurerm_spring_cloud_service.asa.name
    "azureSubscription" = "Azure for Students"
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
    yml_path              = "infrastructure/azure-pipelines.yml"
  }

  variables = {
    "serviceDirectory"  = "classroom-service"
    "serviceName"       = azurerm_spring_cloud_app.classroom_app.name
    "springAppName"     = azurerm_spring_cloud_service.asa.name
    "azureSubscription" = "Azure for Students"
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
    yml_path              = "infrastructure/azure-pipelines.yml"
  }

  variables = {
    "serviceDirectory"  = "api-gateway"
    "serviceName"       = azurerm_spring_cloud_app.gateway_app.name
    "springAppName"     = azurerm_spring_cloud_service.asa.name
    "azureSubscription" = "Azure for Students"
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
    yml_path              = "infrastructure/azure-pipelines.yml"
  }

  variables = {
    "serviceDirectory"  = "discovery-server"
    "serviceName"       = azurerm_spring_cloud_app.discovery_app.name
    "springAppName"     = azurerm_spring_cloud_service.asa.name
    "azureSubscription" = "Azure for Students"
  }
}
