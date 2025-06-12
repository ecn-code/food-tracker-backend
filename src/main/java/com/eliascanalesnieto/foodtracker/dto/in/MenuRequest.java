package com.eliascanalesnieto.foodtracker.dto.in;

import java.util.Date;
import java.util.List;
import java.util.Map;

public record MenuRequest(Date date, String username, Map<String, List<ProductValueRequest>> products) {}
