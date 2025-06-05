package com.eliascanalesnieto.foodtracker.service;

import com.eliascanalesnieto.foodtracker.dto.out.WeeklyMenuResponse;
import com.eliascanalesnieto.foodtracker.repository.WeeklyMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeeklyMenuService {

    private final WeeklyMenuRepository weeklyMenuRepository;

    public WeeklyMenuResponse get(final String username, final String yearWeek) {
        return weeklyMenuRepository.get(username, yearWeek);
    }
}
