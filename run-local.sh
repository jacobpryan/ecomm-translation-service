#!/bin/sh

# Add AWS ENV variables here
# Service will throw errors if not valid
export AWS_ACCESS_KEY_ID=
export AWS_SECRET_ACCESS_KEY=
export AWS_SESSION_TOKEN=

# Do not change the below variables
export AWS_REGION=us-east-1

# It's always a good idea to do a safety build
./gradlew clean build

# Run Gradle Project with local profile
./gradlew bootRun --args='--spring.profiles.active=local'


