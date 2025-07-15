#!/bin/bash
table_name="food-tracker-v2"

awslocal dynamodb create-table --cli-input-json file:///etc/localstack/init/ready.d/food-tracker-v2.json
awslocal dynamodb update-table --cli-input-json file:///etc/localstack/init/ready.d/index-v2.json

files=(
  "user-data.json"
  "unit-data.json"
  "nutritional-information-data.json"
  "recipe-data.json"
  "product-data.json"
  "menu-data.json"
  "setting-data.json"
)

for file in "${files[@]}"; do
  awslocal dynamodb batch-write-item --request-items file:///etc/localstack/init/ready.d/$file
done

echo "DynamoDB table '$table_name' created successfully"
echo "Executed init-dynamodb.sh"
