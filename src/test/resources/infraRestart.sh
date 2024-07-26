#!/bin/bash

docker stop ecomm-translation-service-localstack && docker rm ecomm-translation-service-localstack
sh ./infraStart.sh
