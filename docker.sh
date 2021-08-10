#! /bin/bash
. ./docker.conf
#https://spring.io/guides/gs/spring-boot-docker/

docker build -f app.Dockerfile -t example/app:latest .
#winpty for windows docker flag -i
winpty docker run -it --rm \
-e SERVER_ADDRESS="${EXT_IP}" \
-e SERVER_PORT=8080 \
-e MANAGEMENT_SERVER_PORT=8081 \
-p "${MANAGEMENT_PORT}":8081 \
-p "${PORT}":8080 \
--name testserver \
example/app