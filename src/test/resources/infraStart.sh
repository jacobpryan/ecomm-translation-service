#!/bin/bash

AWS_PAGER=""

### SQS ###
if [ $(docker ps -q -f "name=sf-vproxy-localstack" $1 | wc -l) -eq 0 ] ; then
  echo "Starting localstack"
  docker-compose up -d

  sleep 15

  queue_name="local_ecomm_translation_sqs"
  dlq_name="local_ecomm_translation_sqs_dlq"

  # Executing SQS
  aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name "${queue_name}"
  aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name "${dlq_name}"

else
  echo "localstack was already running"
fi
