variable "db_password" {
  description = "Password for the RDS instance"
  type        = string
}

variable "subnet_ids" {
  description = "List of subnet IDs for the RDS subnet group"
  type        = list(string)
}

variable "sg_id" {
  description = "Security group ID for the RDS instance"
  type        = string
}
