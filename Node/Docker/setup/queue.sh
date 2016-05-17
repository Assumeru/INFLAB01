#!/bin/sh

docker run -d --hostname rabbit --name queue -e RABBITMQ_DEFAULT_USER=0885083 -e RABBITMQ_DEFAULT_PASS=awesomePassword23 -p 15672:15672 -p 5672:5672 rabbitmq:3-management
