package com.eliascanalesnieto.foodtracker.entity;

import com.eliascanalesnieto.foodtracker.utils.DateFormat;
import com.eliascanalesnieto.foodtracker.utils.IdFormat;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.text.ParseException;
import java.util.Date;

@Getter
@Setter
@DynamoDbBean
public class MenuDynamo {

    public static final TableSchema<MenuDynamo> TABLE_SCHEMA = TableSchema.fromBean(MenuDynamo.class);
    public static final Key KEY = Key.builder().partitionValue("MENU_V2").build();

    private String type;
    private String username;
    private Date date;
    private MenuDataDynamo data;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getType() {
        return type;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getDateUsername() {
        return IdFormat.format(DateFormat.format(date), username);
    }

    @DynamoDbAttribute("additional_data")
    public MenuDataDynamo getData() {
        return data;
    }

    @DynamoDbIgnore
    public Date getDate() {
        return date;
    }

    @DynamoDbIgnore
    public String getUsername() {
        return username;
    }

    public void setDateUsername(final String dateUsername) throws ParseException {
        final String[] yearWeekUsernameAttrs = dateUsername.split(IdFormat.SEPARATOR);
        this.date = DateFormat.parse(yearWeekUsernameAttrs[0]);
        this.username = yearWeekUsernameAttrs[1];
    }
}
