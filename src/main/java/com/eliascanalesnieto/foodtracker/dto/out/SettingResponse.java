package com.eliascanalesnieto.foodtracker.dto.out;

import java.util.List;

public record SettingResponse(String version, List<String> partsOfDay) {}
