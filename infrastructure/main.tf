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

# The new service requires Log Analytics and Application Insights
resource "azurerm_log_analytics_workspace" "law" {
  name                = "mesh-log-analytics"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  sku                 = "PerGB2018"
  retention_in_days   = 30
}

resource "azurerm_application_insights" "ai" {
  name                = "mesh-app-insights"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  application_type    = "web"
  workspace_id        = azurerm_log_analytics_workspace.law.id
}

# This is the correct resource for the new environment
resource "azurerm_spring_apps_environment" "asa_env" {
  name                       = "mesh-spring-environment"
  resource_group_name        = azurerm_resource_group.rg.name
  location                   = azurerm_resource_group.rg.location
  log_analytics_workspace_id = azurerm_log_analytics_workspace.law.id
}

# This is the correct resource for the new apps
resource "azurerm_spring_app" "user_app" {
  name                       = "user-service"
  resource_group_name        = azurerm_resource_group.rg.name
  spring_apps_environment_id = azurerm_spring_apps_environment.asa_env.id
}

resource "azurerm_spring_app" "admin_app" {
  name                       = "admin-service"
  resource_group_name        = azurerm_resource_group.rg.name
  spring_apps_environment_id = azurerm_spring_apps_environment.asa_env.id
}

resource "azurerm_spring_app" "classroom_app" {
  name                       = "classroom-service"
  resource_group_name        = azurerm_resource_group.rg.name
  spring_apps_environment_id = azurerm_spring_apps_environment.asa_env.id
}

resource "azurerm_spring_app" "gateway_app" {
  name                       = "api-gateway"
  resource_group_name        = azurerm_resource_group.rg.name
  spring_apps_environment_id = azurerm_spring_apps_environment.asa_env.id
}

resource "azurerm_spring_app" "discovery_app" {
  name                       = "discovery-server"
  resource_group_name        = azurerm_resource_group.rg.name
  spring_apps_environment_id = azurerm_spring_apps_environment.asa_env.id
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
    # This now points to the service-specific YAML file
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
    # This now points to the service-specific YAML file
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
