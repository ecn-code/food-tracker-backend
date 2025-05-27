package com.eliascanalesnieto.entity;

import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
@ToString
@Setter
public class UserDynamo {

    public static final TableSchema<UserDynamo> TABLE_SCHEMA = TableSchema.fromBean(UserDynamo.class);

    private String pk;
    private String sk;
    private String password;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() {
        return pk;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() {
        return sk;
    }

    @DynamoDbAttribute("password")
    public String getPassword() {
        return password;
    }
}
