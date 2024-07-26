#!/bin/sh

#rebuild the project from a clean spot
./gradlew clean build
pid=$!
wait $pid

#Build the docker container from the gradle image
docker build -t vapi .
pid=$!
wait $pid

#Start the docker container
docker run -it -e SPRING_PROFILE="local" -p 8080:8080 vapi
pid=$!
wait $pid