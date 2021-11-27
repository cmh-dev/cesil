#!/usr/bin/env sh
./gradlew clean build :cesil-web:shadowJar
docker build --no-cache -t cmhdev/cesil ./cesil-web/