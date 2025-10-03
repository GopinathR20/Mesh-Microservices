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
  location = "East US"
}

resource "azurerm_spring_cloud_service" "asa" {
  name                = "mesh-spring-apps-env"
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku_name            = "Basic"
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
resource "azuredevops_build_definition" "user_pipeline" {
  project_id = data.azuredevops_project.project.id
  name       = "UserService-CI"

  repository {
    repo_type             = "GitHub"
    repo_id               = "GopinathR20/Mesh-Microservices"
    branch_name           = "main"
    service_connection_id = "GopinathR20"
  }

  yaml {
    content = yamlencode({
      trigger = {
        branches = { include = ["main"] },
        paths    = { include = ["Mesh-Microservices/user-service/*"], exclude = ["infrastructure/*"] }
      },
      pool = { vmImage = "ubuntu-latest" },
      steps = [
        { task = "Maven@3", inputs = { mavenPomFile = "Mesh-Microservices/user-service/pom.xml", goals = "package" } },
        { task = "AzureSpringCloud@1", inputs = { azureSubscription = "Azure for Students", Action = "Deploy", AzureSpringCloud = azurerm_spring_cloud_service.asa.name, AppName = azurerm_spring_cloud_app.user_app.name, JarFileOrFolderPath = "**/target/*.jar" } }
      ]
    })
  }
}

# --- Pipeline for Admin Service ---
resource "azuredevops_build_definition" "admin_pipeline" {
  project_id = data.azuredevops_project.project.id
  name       = "AdminService-CI"

  repository {
    repo_type             = "GitHub"
    repo_id               = "GopinathR20/Mesh-Microservices"
    branch_name           = "main"
    service_connection_id = "GopinathR20"
  }

  yaml {
    content = yamlencode({
      trigger = {
        branches = { include = ["main"] },
        paths    = { include = ["Mesh-Microservices/admin-service/*"], exclude = ["infrastructure/*"] }
      },
      pool = { vmImage = "ubuntu-latest" },
      steps = [
        { task = "Maven@3", inputs = { mavenPomFile = "Mesh-Microservices/admin-service/pom.xml", goals = "package" } },
        { task = "AzureSpringCloud@1", inputs = { azureSubscription = "Azure for Students", Action = "Deploy", AzureSpringCloud = azurerm_spring_cloud_service.asa.name, AppName = azurerm_spring_cloud_app.admin_app.name, JarFileOrFolderPath = "**/target/*.jar" } }
      ]
    })
  }
}

# --- Pipeline for Classroom Service ---
resource "azuredevops_build_definition" "classroom_pipeline" {
  project_id = data.azuredevops_project.project.id
  name       = "ClassroomService-CI"

  repository {
    repo_type             = "GitHub"
    repo_id               = "GopinathR20/Mesh-Microservices"
    branch_name           = "main"
    service_connection_id = "GopinathR20"
  }

  yaml {
    content = yamlencode({
      trigger = {
        branches = { include = ["main"] },
        paths    = { include = ["Mesh-Microservices/classroom-service/*"], exclude = ["infrastructure/*"] }
      },
      pool = { vmImage = "ubuntu-latest" },
      steps = [
        { task = "Maven@3", inputs = { mavenPomFile = "Mesh-Microservices/classroom-service/pom.xml", goals = "package" } },
        { task = "AzureSpringCloud@1", inputs = { azureSubscription = "Azure for Students", Action = "Deploy", AzureSpringCloud = azurerm_spring_cloud_service.asa.name, AppName = azurerm_spring_cloud_app.classroom_app.name, JarFileOrFolderPath = "**/target/*.jar" } }
      ]
    })
  }
}

# --- Pipeline for API Gateway ---
resource "azuredevops_build_definition" "gateway_pipeline" {
  project_id = data.azuredevops_project.project.id
  name       = "ApiGateway-CI"

  repository {
    repo_type             = "GitHub"
    repo_id               = "GopinathR20/Mesh-Microservices"
    branch_name           = "main"
    service_connection_id = "GopinathR20"
  }

  yaml {
    content = yamlencode({
      trigger = {
        branches = { include = ["main"] },
        paths    = { include = ["Mesh-Microservices/api-gateway/*"], exclude = ["infrastructure/*"] }
      },
      pool = { vmImage = "ubuntu-latest" },
      steps = [
        { task = "Maven@3", inputs = { mavenPomFile = "Mesh-Microservices/api-gateway/pom.xml", goals = "package" } },
        { task = "AzureSpringCloud@1", inputs = { azureSubscription = "Azure for Students", Action = "Deploy", AzureSpringCloud = azurerm_spring_cloud_service.asa.name, AppName = azurerm_spring_cloud_app.gateway_app.name, JarFileOrFolderPath = "**/target/*.jar" } }
      ]
    })
  }
}

# --- Pipeline for Discovery Server ---
resource "azuredevops_build_definition" "discovery_pipeline" {
  project_id = data.azuredevops_project.project.id
  name       = "DiscoveryServer-CI"

  repository {
    repo_type             = "GitHub"
    repo_id               = "GopinathR20/Mesh-Microservices"
    branch_name           = "main"
    service_connection_id = "GopinathR20"
  }

  yaml {
    content = yamlencode({
      trigger = {
        branches = { include = ["main"] },
        paths    = { include = ["Mesh-Microservices/discovery-server/*"], exclude = ["infrastructure/*"] }
      },
      pool = { vmImage = "ubuntu-latest" },
      steps = [
        { task = "Maven@3", inputs = { mavenPomFile = "Mesh-Microservices/discovery-server/pom.xml", goals = "package" } },
        { task = "AzureSpringCloud@1", inputs = { azureSubscription = "Azure for Students", Action = "Deploy", AzureSpringCloud = azurerm_spring_cloud_service.asa.name, AppName = azurerm_spring_cloud_app.discovery_app.name, JarFileOrFolderPath = "**/target/*.jar" } }
      ]
    })
  }
}