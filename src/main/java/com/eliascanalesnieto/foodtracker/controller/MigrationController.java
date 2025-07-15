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

    @GetMapping
    public void migrate() {
        migrationRepository.migrateUsers();
        migrationRepository.migrateSettings();
        migrationRepository.migrateUnits();
        migrationRepository.migrateNutritionalValue();
        migrationRepository.migrateProductsAndRecipes();
        migrationRepository.migrateWeeklyMenus();
    }

}
