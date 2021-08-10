FROM openjdk:11
VOLUME /tmp
ARG DEPENDENCY=target/dependency
RUN mkdir -p service
RUN mkdir -p certs
COPY ./certs/ /certs
COPY ${DEPENDENCY}/BOOT-INF/lib /service/lib
COPY ${DEPENDENCY}/META-INF /service/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /service
ENTRYPOINT ["java", "-cp","service:service/lib/*","edu.strauteka.example.ExampleApplication"]
