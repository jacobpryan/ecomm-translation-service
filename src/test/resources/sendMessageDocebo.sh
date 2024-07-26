#!/bin/bash

aws --endpoint-url=http://localhost:4566 sqs send-message \
  --queue-url http://localhost:4566/000000000000/local_sf_vproxy_sqs \
  --message-body "{\"actor\":{\"name\":\"Samantha Adams\",\"account\":{\"homePage\":\"https://learn.northwesternmutual.com/\",\"name\":\"L8BB0I5CE\"},\"objectType\":\"Agent\"},\"result\":{\"duration\": \"PT15S\"},\"verb\":{\"id\":\"http://adlnet.gov/expapi/verbs/completed\",\"display\":{\"en-US\":\"completed\"}},\"object\":{\"id\":\"https://learn.northwesternmutual.com/learn/course/99999\",\"definition\":{\"name\":{\"en-US\":\"Local Course\"},\"description\":{\"en-US\":\"Local Course desc\"},\"extensions\":{\"https://nm.com/externalId\":\"localTestCode\"}}},\"timestamp\":\"2021-12-08T09:13:29.000-06:04\"}" \
  --message-attributes '{ "Vendor":{ "DataType":"String","StringValue":"Docebo" } }'
