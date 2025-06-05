package com.eliascanalesnieto.foodtracker.controller;

import com.eliascanalesnieto.foodtracker.annotations.Auth;
import com.eliascanalesnieto.foodtracker.dto.out.WeeklyMenuResponse;
import com.eliascanalesnieto.foodtracker.model.User;
import com.eliascanalesnieto.foodtracker.service.WeeklyMenuService;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
public class WeeklyMenuController {

    private final WeeklyMenuService weeklyMenuService;

    public WeeklyMenuResponse get(
            @Auth User currentUser,
            @QueryParam("username") final String username,
            @QueryParam("year_week") final String yearWeek
    ) {
        return weeklyMenuService.get(username, yearWeek);
    }

}
