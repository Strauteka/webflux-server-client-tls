#! /bin/bash
. ./docker.conf
#https://spring.io/guides/gs/spring-boot-docker/

docker build -f app.Dockerfile -t example/app:latest .

winpty docker run -it --rm -e SERVER_PORT=8080 -e SERVER_ADDRESS="${EXT_IP}" -p "${PORT}":8080 --name testserver example/app