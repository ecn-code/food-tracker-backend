package com.eliascanalesnieto.foodtracker.entity;

import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.Key;
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
    public static final Key KEY = Key.builder().partitionValue("USERV2").build();

    private String type;
    private String username;
    private String chatId;
    private String lastCode;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getType() {
        return type;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getUsername() {
        return username;
    }

    @DynamoDbAttribute("chat_id")
    public String getChatId() {
        return chatId;
    }

    @DynamoDbAttribute("last_code")
    public String getLastCode() {
        return lastCode;
    }
}
