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
#  AZURE INFRASTRUCTURE
# ------------------------------------------------------------------

resource "azurerm_resource_group" "rg" {
  name     = "mesh-project-rg"
  location = "Central India"
}

resource "azurerm_container_registry" "acr" {
  name                = "meshregistry${substr(md5(azurerm_resource_group.rg.name), 0, 5)}"
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku                 = "Basic"
  admin_enabled       = true
}

resource "azurerm_container_app_environment" "aca_env" {
  name                = "mesh-aca-env"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
}

# Create container apps with a minimal placeholder template
resource "azurerm_container_app" "gateway_app" {
  name                         = "api-gateway-app"
  container_app_environment_id = azurerm_container_app_environment.aca_env.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"
  template {
    container {
      name   = "placeholder"
      image  = "mcr.microsoft.com/k8se/apps/null-image:latest"
      cpu    = 0.25
      memory = "0.5Gi"
    }
  }
}

resource "azurerm_container_app" "user_app" {
  name                         = "user-service-app"
  container_app_environment_id = azurerm_container_app_environment.aca_env.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"
  template {
    container {
      name   = "placeholder"
      image  = "mcr.microsoft.com/k8se/apps/null-image:latest"
      cpu    = 0.25
      memory = "0.5Gi"
    }
  }
}

resource "azurerm_container_app" "admin_app" {
  name                         = "admin-service-app"
  container_app_environment_id = azurerm_container_app_environment.aca_env.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"
  template {
    container {
      name   = "placeholder"
      image  = "mcr.microsoft.com/k8se/apps/null-image:latest"
      cpu    = 0.25
      memory = "0.5Gi"
    }
  }
}

resource "azurerm_container_app" "classroom_app" {
  name                         = "classroom-service-app"
  container_app_environment_id = azurerm_container_app_environment.aca_env.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"
  template {
    container {
      name   = "placeholder"
      image  = "mcr.microsoft.com/k8se/apps/null-image:latest"
      cpu    = 0.25
      memory = "0.5Gi"
    }
  }
}

resource "azurerm_container_app" "discovery_app" {
  name                         = "discovery-server-app"
  container_app_environment_id = azurerm_container_app_environment.aca_env.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"
  template {
    container {
      name   = "placeholder"
      image  = "mcr.microsoft.com/k8se/apps/null-image:latest"
      cpu    = 0.25
      memory = "0.5Gi"
    }
  }
}

# ------------------------------------------------------------------
#  AZURE DEVOPS PIPELINES
# ------------------------------------------------------------------

data "azuredevops_project" "project" {
  name = "Mesh"
}

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