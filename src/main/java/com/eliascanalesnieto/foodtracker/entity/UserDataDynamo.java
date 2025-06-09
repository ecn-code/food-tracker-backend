package com.eliascanalesnieto.foodtracker.entity;

import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Setter
@ToString
@DynamoDbBean
public class UserDataDynamo {

    private String chatId;
    private String lastCode;

    @DynamoDbAttribute("chat_id")
    public String getChatId() {
        return chatId;
    }

    @DynamoDbAttribute("last_code")
    public String getLastCode() {
        return lastCode;
    }
}
