#! /bin/bash
. ./docker.conf

bash cert.sh "san=ip:${EXT_IP}"

mvn clean install
mkdir target/dependency
cd target/dependency || exit
jar -xf ../*.jar
cd ../../
#create docker image and run container
bash docker.sh