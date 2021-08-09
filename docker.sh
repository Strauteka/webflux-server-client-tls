
. ./docker.conf
#https://spring.io/guides/gs/spring-boot-docker/

function pause(){
 read  -r -s -n 1 -p "Press any key to continue . . ."
 echo ""
}

bash ./cert.sh "san=ip:${EXT_IP}"
mvn clean install
mkdir target/dependency
cd target/dependency || exit
jar -xf ../*.jar
cd ../../
docker build -f app.Dockerfile -t example/app:latest .
#  -p 8080:8080 discarding when using host
#--network="host"
docker run --rm -p "${PORT}":"${PORT}" --name testserver -e "JAVA_OPTS=-Dserver.port=${PORT} -Dserver.address=${EXT_IP}"   example/app

pause