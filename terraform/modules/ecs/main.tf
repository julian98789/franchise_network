resource "aws_ecr_repository" "repo" {
  name = "franchise-app-repo"
}

resource "aws_ecs_cluster" "cluster" {
  name = "franchise-cluster"
}

resource "aws_cloudwatch_log_group" "logs" {
  name              = "/ecs/franchise-service"
  retention_in_days = 7
}

resource "aws_lb" "alb" {
  name               = "franchise-alb"
  internal           = false
  load_balancer_type = "application"
  subnets            = var.subnet_ids
  security_groups    = [var.ecs_sg_id]
}

resource "aws_lb_target_group" "tg" {
  name        = "franchise-tg"
  port        = 8085
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
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

resource "aws_ecs_task_definition" "task" {
  family                   = "franchise-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = var.execution_role_arn

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
        value = "r2dbc:mysql://${var.db_address}:${var.db_port}/franquicia"
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
    subnets         = var.subnet_ids
    security_groups = [var.ecs_sg_id]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.tg.arn
    container_name   = "franchise-app"
    container_port   = 8085
  }
}

output "alb_dns_name" {
  value = aws_lb.alb.dns_name
}
