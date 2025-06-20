package com.eliascanalesnieto.foodtracker.entity;

import com.eliascanalesnieto.foodtracker.utils.IdFormat;
import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Setter
@ToString
@DynamoDbBean
public class WeeklyMenuDynamo {

    public WeeklyMenuDynamo() {
        type = KEY.partitionKeyValue().s();
    }

    public static final TableSchema<WeeklyMenuDynamo> TABLE_SCHEMA = TableSchema.fromBean(WeeklyMenuDynamo.class);
    public static final Key KEY = Key.builder().partitionValue("WEEKLY_MENU_V2").build();

    private String type;
    private String yearWeek;
    private String username;
    private WeeklyMenuDataDynamo data;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getType() {
        return type;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getYearWeekUsername() {
        return IdFormat.format(yearWeek, username);
    }

    @DynamoDbAttribute("additional_data")
    public WeeklyMenuDataDynamo getData() {
        return data;
    }

    public void setYearWeekUsername(final String yearWeekUsername) {
        final String[] yearWeekUsernameAttrs = yearWeekUsername.split(IdFormat.SEPARATOR);
        this.yearWeek = yearWeekUsernameAttrs[0];
        this.username = yearWeekUsernameAttrs[1];
    }

}
