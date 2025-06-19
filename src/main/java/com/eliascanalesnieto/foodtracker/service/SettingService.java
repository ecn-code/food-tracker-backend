package com.eliascanalesnieto.foodtracker.service;

import com.eliascanalesnieto.foodtracker.entity.SettingDynamo;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.repository.SettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final SettingRepository settingRepository;

    public SettingDynamo get(final String version) throws EntityNotFoundException {
        return settingRepository.get(version)
                .orElseThrow(EntityNotFoundException::new);
    }
}

