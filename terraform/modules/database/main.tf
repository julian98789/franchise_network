resource "aws_db_subnet_group" "db_subnets" {
  name       = "franchise-db-subnet-group"
  subnet_ids = var.subnet_ids
}

resource "aws_db_instance" "db" {
  identifier              = "franchise-db"
  instance_class          = "db.t3.micro"
  engine                  = "mysql"
  engine_version          = "8.0"
  username                = "admin"
  password                = var.db_password
  allocated_storage       = 20
  db_name                 = "franquicia"
  skip_final_snapshot     = true
  publicly_accessible     = true
  deletion_protection     = false
  multi_az                = false

  db_subnet_group_name   = aws_db_subnet_group.db_subnets.name
  vpc_security_group_ids = [var.sg_id]
}

output "rds_endpoint" {
  value = aws_db_instance.db.address
}

output "rds_port" {
  value = aws_db_instance.db.port
}
