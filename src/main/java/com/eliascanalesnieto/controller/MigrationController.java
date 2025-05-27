package com.eliascanalesnieto.controller;

import com.eliascanalesnieto.repository.DynamoDBClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/migrations")
@Slf4j
@RequiredArgsConstructor
public class MigrationController {

    private final DynamoDBClient dynamoDBClient;

    @GetMapping("/users")
    public void users() {
        log.debug("Migrating users");
    }

}
