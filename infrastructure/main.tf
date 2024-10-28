## Deploy App runner, pulling from ECR.

## Create permissions


## Create App Runner
resource "aws_apprunner_service" "example" {
  service_name = "aws_app_runner_service_" + var.unique_id

  source_configuration {
    image_repository {
      image_configuration {
        port = "8000"
        runtime_environment_variables = {
          "CLUSTER_API_KEY" = var.cloud_cluster_api_key
          "CLUSTER_API_SECRET" = var.cloud_cluster_api_secret
        }
      }
      image_identifier      = "195571588534.dkr.ecr.eu-west-2.amazonaws.com/chrisp1985_ecr_docker_repo:f5983e69ce6e90f80a01a60cffb57051a27b5c7d"
      image_repository_type = "ECR"
    }
    auto_deployments_enabled = true
  }

  tags = {
    Name = "user-service-apprunner-service"
  }
}