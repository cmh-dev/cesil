FROM openjdk:11-jre-slim
COPY ./build/libs/cesil-web.jar /usr/app/
WORKDIR /usr/app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "cesil-web.jar"]
