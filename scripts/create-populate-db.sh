#!/bin/bash

DYNAMO_URL=http://dynamodb:8000

docker run --rm -it --network=python -e AWS_ACCESS_KEY_ID=X -e AWS_SECRET_ACCESS_KEY=X -v ${PWD}/data:/root/ amazon/aws-cli dynamodb delete-table --table-name food-tracker-v2 --region us-west-1 --endpoint-url $DYNAMO_URL
docker run --rm -it --network=python -e AWS_ACCESS_KEY_ID=X -e AWS_SECRET_ACCESS_KEY=X -v ${PWD}/data:/root/ amazon/aws-cli dynamodb create-table --cli-input-json file:///root/food-tracker-v2.json --region us-west-1 --endpoint-url $DYNAMO_URL
docker run --rm -it --network=python -e AWS_ACCESS_KEY_ID=X -e AWS_SECRET_ACCESS_KEY=X -v ${PWD}/data:/root/ amazon/aws-cli  dynamodb update-table --cli-input-json file:///root/index-v2.json --region us-west-1 --endpoint-url $DYNAMO_URL