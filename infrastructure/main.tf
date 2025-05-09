## Deploy App runner, pulling from ECR.
terraform {
  required_version = ">= 0.12"
}

provider "aws" {
  region = "eu-west-2"
  access_key = var.aws_access_key
  secret_key = var.aws_access_secret
}

## Create permissions
resource "aws_iam_role" "apprunner_iam_role" {
  name = "apprunner_iam_role"
  assume_role_policy = jsonencode({
    "Version": "2012-10-17",
    "Statement": [
      {
        "Effect": "Allow",
        "Principal": {
          "Service": "build.apprunner.amazonaws.com"
        },
        "Action": "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "myrolespolicy" {
  role = aws_iam_role.apprunner_iam_role.id
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSAppRunnerServicePolicyForECRAccess"
}

resource "time_sleep" "waitrolecreate" {
  depends_on = [aws_iam_role.apprunner_iam_role]
  create_duration = "10s"
}

## Create App Runner
resource "aws_apprunner_service" "example" {
  service_name = "aws_app_runner_service"

  source_configuration {
    authentication_configuration {
      access_role_arn = aws_iam_role.apprunner_iam_role.arn
    }
    image_repository {
      image_configuration {
        port = "8080"
        runtime_environment_variables = {
          "CLUSTER_API_KEY" = var.confluent_key
          "CLUSTER_API_SECRET" = var.confluent_secret
          "SCHEMA_API_KEY" = var.confluent_schema_key
          "SCHEMA_API_SECRET" = var.confluent_schema_secret
        }
      }
      image_identifier      = format("%s:%s","195571588534.dkr.ecr.eu-west-2.amazonaws.com/chrisp1985_ecr_docker_repo",var.image_tag)
      image_repository_type = "ECR"
    }
    auto_deployments_enabled = true
  }
#
#  health_check_configuration {
#    path = "/actuator/health"
#    healthy_threshold = 3
#    unhealthy_threshold = 5
#    interval = 5
#  }

  tags = {
    Name = "user-service-apprunner-service"
  }
}
