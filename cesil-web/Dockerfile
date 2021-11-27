FROM eclipse-temurin:11.0.13_8-jre-alpine@sha256:6bb47aba5cebd4cb5ae09b64a54bcaf4fa5903dde0526a664e0b20528336075b
RUN mkdir /app
RUN addgroup --system javauser && adduser -S -s /bin/false -G javauser javauser
COPY ./build/libs/cesil-web.jar /app/
WORKDIR /app
RUN chown -R javauser:javauser /app
USER javauser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "cesil-web.jar"]
