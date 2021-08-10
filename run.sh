#! /bin/bash
. ./docker.conf
echo "Creating certs"
bash cert.sh "san=ip:${EXT_IP}"
echo "Compiling and extracting jar"
bash jar.sh
echo "Creating docker image and running container"
bash docker.sh