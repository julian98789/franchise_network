terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  required_version = ">= 1.6.0"
}

provider "aws" {
  region = "us-east-1"
}


data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}


data "aws_iam_role" "ecs_task_execution" {
  name = "ecsTaskExecutionRole"
}


resource "aws_security_group" "ecs_alb_sg" {
  name        = "ecs-alb-sg"
  description = "Allow HTTP and container access"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description = "HTTP desde internet"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Puerto de la app (8085)"
    from_port   = 8085
    to_port     = 8085
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}


resource "aws_security_group" "rds_sg" {
  name        = "rds-mysql-sg"
  description = "Allow MySQL from ECS and local IP"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description = "MySQL desde IP local"
    from_port   = 3306
    to_port     = 3306
    protocol    = "tcp"
    cidr_blocks = ["191.95.148.18/32"]
  }

  ingress {
    description     = "MySQL desde ECS"
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs_alb_sg.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}


resource "aws_db_subnet_group" "db_subnets" {
  name       = "franchise-db-subnet-group"
  subnet_ids = data.aws_subnets.default.ids
}


resource "aws_db_instance" "franchise_db" {
  identifier              = "franchise-db"
  instance_class          = "db.t3.micro"
  engine                  = "mysql"
  engine_version          = "8.0"
  username                = "admin"
  password                = var.db_password
  allocated_storage       = 20
  skip_final_snapshot     = true
  publicly_accessible     = true
  db_name                 = "franquicia"
  db_subnet_group_name    = aws_db_subnet_group.db_subnets.name
  vpc_security_group_ids  = [aws_security_group.rds_sg.id]
  deletion_protection     = false
  multi_az                = false
}


resource "aws_ecr_repository" "repo" {
  name = "franchise-app-repo"
}


resource "aws_ecs_cluster" "cluster" {
  name = "franchise-cluster"
}


resource "aws_lb" "alb" {
  name               = "franchise-alb"
  internal           = false
  load_balancer_type = "application"
  subnets            = data.aws_subnets.default.ids
  security_groups    = [aws_security_group.ecs_alb_sg.id]
}

resource "aws_lb_target_group" "tg" {
  name        = "franchise-tg"
  port        = 8085
  protocol    = "HTTP"
  vpc_id      = data.aws_vpc.default.id
  target_type = "ip"

  health_check {
    path                = "/health"
    matcher             = "200"
    interval            = 30
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 3
  }
}

resource "aws_lb_listener" "listener" {
  load_balancer_arn = aws_lb.alb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.tg.arn
  }
}


resource "aws_cloudwatch_log_group" "franchise_logs" {
  name              = "/ecs/franchise-service"
  retention_in_days = 7
}


resource "aws_ecs_task_definition" "task" {
  family                   = "franchise-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = data.aws_iam_role.ecs_task_execution.arn

  container_definitions = jsonencode([{
    name  = "franchise-app"
    image = "${aws_ecr_repository.repo.repository_url}:latest"
    portMappings = [{
      containerPort = 8085
      hostPort      = 8085
    }]
    environment = [
      {
        name  = "SPRING_R2DBC_URL"
        value = "r2dbc:mysql://${aws_db_instance.franchise_db.address}:${aws_db_instance.franchise_db.port}/franquicia"
      },
      {
        name  = "SPRING_R2DBC_USERNAME"
        value = "admin"
      },
      {
        name  = "SPRING_R2DBC_PASSWORD"
        value = var.db_password
      }
    ]
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = "/ecs/franchise-service"
        awslogs-region        = "us-east-1"
        awslogs-stream-prefix = "ecs"
      }
    }
  }])
}


resource "aws_ecs_service" "service" {
  name            = "franchise-service"
  cluster         = aws_ecs_cluster.cluster.id
  task_definition = aws_ecs_task_definition.task.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets         = data.aws_subnets.default.ids
    security_groups = [aws_security_group.ecs_alb_sg.id]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.tg.arn
    container_name   = "franchise-app"
    container_port   = 8085
  }
}


output "rds_endpoint" {
  value = aws_db_instance.franchise_db.address
}

output "rds_port" {
  value = aws_db_instance.franchise_db.port
}

output "alb_dns_name" {
  value = aws_lb.alb.dns_name
}
