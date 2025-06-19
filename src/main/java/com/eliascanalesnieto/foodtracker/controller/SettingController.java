package com.eliascanalesnieto.foodtracker.controller;

import com.eliascanalesnieto.foodtracker.dto.out.SettingResponse;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @GetMapping("/{version}")
    public SettingResponse get(@PathVariable String version) throws EntityNotFoundException {
        var setting = settingService.get(version);
        return new SettingResponse(setting.getVersion(), setting.getData().getPartsOfDay());
    }
}
