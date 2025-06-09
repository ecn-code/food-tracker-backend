#!/bin/bash
table_name="food-tracker-v2"

awslocal dynamodb create-table --cli-input-json file:///etc/localstack/init/ready.d/food-tracker-v2.json
awslocal dynamodb batch-write-item --request-items file:///etc/localstack/init/ready.d/user-data.json
awslocal dynamodb batch-write-item --request-items file:///etc/localstack/init/ready.d/unit-data.json
awslocal dynamodb batch-write-item --request-items file:///etc/localstack/init/ready.d/nutritional-information-data.json
awslocal dynamodb batch-write-item --request-items file:///etc/localstack/init/ready.d/recipe-data.json

echo "DynamoDB table '$table_name' created successfully"
echo "Executed init-dynamodb.sh"
