FROM fabric8/java-alpine-openjdk11-jre:1.8
VOLUME /tmp
ARG DEPENDENCY=target/dependency
RUN mkdir -p service
RUN mkdir -p certs
COPY ./certs/ /certs
COPY ${DEPENDENCY}/BOOT-INF/lib /service/lib
COPY ${DEPENDENCY}/META-INF /service/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /service
#allows run docker run -p 9000:9000 -e JAVA_OPTS=-Dserver.port=9000 --rm example/app
ENTRYPOINT java $JAVA_OPTS -cp "service:service/lib/*" "edu.strauteka.example.ExampleApplication"
#ENTRYPOINT ["java","$JAVA_OPTS", "-cp","service:service/lib/*","edu.strauteka.example.ExampleApplication"]
