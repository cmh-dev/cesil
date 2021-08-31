FROM openjdk:11
COPY ./cesil-web/build/libs/cesil-web.jar /usr/app/
WORKDIR /usr/app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "cesil-web.jar"]
