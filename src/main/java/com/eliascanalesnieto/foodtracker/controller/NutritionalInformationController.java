package com.eliascanalesnieto.foodtracker.controller;

import com.eliascanalesnieto.foodtracker.annotations.Auth;
import com.eliascanalesnieto.foodtracker.dto.in.NutritionalInformationRequest;
import com.eliascanalesnieto.foodtracker.dto.out.NutritionalInformationResponse;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.exception.UnprocessableContent;
import com.eliascanalesnieto.foodtracker.model.User;
import com.eliascanalesnieto.foodtracker.service.NutritionalInformationService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nutritional-information")
@RequiredArgsConstructor
public class NutritionalInformationController {

    private final NutritionalInformationService nutritionalInformationService;

    @GetMapping
    public List<NutritionalInformationResponse> get(@Auth final User currentUser) {
        return nutritionalInformationService.get();
    }

    @GetMapping("/{id}")
    public NutritionalInformationResponse get(@Auth final User currentUser, @PathVariable final String id) throws EntityNotFoundException {
        return nutritionalInformationService.get(id);
    }

    @PostMapping
    public NutritionalInformationResponse post(@Auth final User currentUser, @RequestBody final NutritionalInformationRequest request) throws UnprocessableContent {
        validate(request);
        return nutritionalInformationService.post(request);
    }

    @PutMapping("/{id}")
    public NutritionalInformationResponse put(@Auth final User currentUser, @PathVariable final String id, @RequestBody final NutritionalInformationRequest request) throws UnprocessableContent {
        validate(request);
        return nutritionalInformationService.put(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@Auth final User currentUser, @PathVariable final String id) {
        nutritionalInformationService.delete(id);
    }

    private void validate(final NutritionalInformationRequest request) throws UnprocessableContent {
        if (!StringUtils.hasText(request.name()) || !StringUtils.hasText(request.shortName()) ||
                !StringUtils.hasText(request.unit())) {
            throw new UnprocessableContent();
        }
    }
}
