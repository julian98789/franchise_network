
variable "subnet_ids" {
  description = "Subnets where the ECS service will run"
  type        = list(string)
}

variable "vpc_id" {
  description = "VPC ID for ECS and ALB resources"
  type        = string
}

variable "ecs_sg_id" {
  description = "Security group ID for ECS tasks and ALB"
  type        = string
}

variable "db_address" {
  description = "Database endpoint address"
  type        = string
}

variable "db_port" {
  description = "Database port"
  type        = number
}

variable "db_password" {
  description = "Database password"
  type        = string
}

variable "execution_role_arn" {
  description = "ARN of the ECS execution IAM role"
  type        = string
}
