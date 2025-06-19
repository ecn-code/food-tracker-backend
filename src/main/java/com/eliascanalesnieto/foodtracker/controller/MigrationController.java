package com.eliascanalesnieto.foodtracker.controller;

import com.eliascanalesnieto.foodtracker.repository.MigrationRepository;
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

    private final MigrationRepository migrationRepository;

    @GetMapping("/users")
    public void users() {
        migrationRepository.migrateUsers();
    }

    @GetMapping("/weekly-menus")
    public void weeklyMenus() {
        migrationRepository.migrateWeeklyMenus();
    }

    @GetMapping("/nutritional-value")
    public void nutritionalValue() {
        migrationRepository.migrateNutritionalValue();
    }

    @GetMapping("/settings")
    public void settings() {
        migrationRepository.migrateSettings();
    }

}
