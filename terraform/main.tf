terraform {
  required_version = ">= 1.6.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "us-east-1"
}


module "network" {
  source = "./modules/network"
}


module "security" {
  source  = "./modules/security"
  vpc_id  = module.network.vpc_id
}


module "database" {
  source      = "./modules/database"
  db_password = var.db_password
  subnet_ids  = module.network.subnet_ids
  sg_id       = module.security.rds_sg_id
}


module "ecs" {
  source            = "./modules/ecs"
  subnet_ids        = module.network.subnet_ids
  vpc_id            = module.network.vpc_id
  ecs_sg_id         = module.security.ecs_sg_id
  db_address        = module.database.rds_endpoint
  db_port           = module.database.rds_port
  db_password       = var.db_password
  execution_role_arn = data.aws_iam_role.ecs_task_execution.arn
}

data "aws_iam_role" "ecs_task_execution" {
  name = "ecsTaskExecutionRole"
}

output "rds_endpoint" {
  value = module.database.rds_endpoint
}

output "rds_port" {
  value = module.database.rds_port
}

output "alb_dns_name" {
  value = module.ecs.alb_dns_name
}
