#!/bin/bash

aws --endpoint-url=http://localhost:4566 sqs send-message \
  --queue-url http://localhost:4566/000000000000/local_sf_vproxy_sqs \
  --message-body "{\"actor\":{\"name\":\"Samantha Adams\",\"account\":{\"homePage\":\"https://nwmutual.plateau.com/\",\"name\":\"bf31b4e8-2507-4a37-9094-f3425a98a1fe\"},\"objectType\":\"Agent\"},\"verb\":{\"id\":\"http://adlnet.gov/expapi/verbs/completed\",\"display\":{\"en-US\":\"completed\"}},\"object\":{\"id\":\"https://nwmutual.plateau.com/FD_Building_Clientele_v3\",\"definition\":{\"name\":{\"en-US\":\"Building a Clientele\"}}},\"timestamp\":\"2021-12-08T09:13:29.000-06:04\"}" \
  --message-attributes '{ "Vendor":{ "DataType":"String","StringValue":"Success Factors" } }'
