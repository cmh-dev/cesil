#!/usr/bin/env sh
./gradlew clean assemble
./gradlew :cesil-interpreter:test
./gradlew :cesil-cli:test
./gradlew :cesil-web:test
